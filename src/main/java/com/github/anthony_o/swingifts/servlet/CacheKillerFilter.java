package com.github.anthony_o.swingifts.servlet;

import com.github.anthony_o.swingifts.util.Base64Utils;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.BooleanUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebFilter(
        filterName = "CacheKillerFilter",
        urlPatterns = {
                "*.html",
                "*.css",
                "*.js",
                "*.woff2"
        })
public class CacheKillerFilter implements Filter {

    private Map<String, String> uriToHashMap = new HashMap<>();
    private boolean resourcesCanChange;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        resourcesCanChange = BooleanUtils.toBoolean(System.getProperty("cacheKillerFilter.resourcesCanChange", "false"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestHash = httpRequest.getParameter("v");

        if (requestHash == null) {
            // No version hash specified, redirect to version
            redirectToHashUrlIfRequestIsValid(httpRequest, httpResponse, chain);
        } else {
            String computedHash = retrieveHashForRequestURIIfValidElseHandleRequest(httpRequest, httpResponse, chain);
            if (computedHash != null) {
                if (computedHash.equals(requestHash)) {
                    // both hash are the same, we can handle the request as usual
                    chain.doFilter(request, response);
                } else {
                    // the hash are different, we must redirect to an url with the right hash
                    redirectToHashUrl(httpRequest, httpResponse, chain, computedHash);
                }
            } else {
                // No hash was computed for the request URI, that means that the request was not valid and was already handled, so do nothing
            }
        }
    }

    private void redirectToHashUrlIfRequestIsValid(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException {
        String hash = retrieveHashForRequestURIIfValidElseHandleRequest(httpRequest, httpResponse, chain);
        if (hash != null) {
            // hash was computed so the request was OK and not handled, let's redirect it
            redirectToHashUrl(httpRequest, httpResponse, chain, hash);
        } else {
            // no hash computed that means that the request was not OK and was already handled
        }
    }

    private String retrieveHashForRequestURIIfValidElseHandleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException {
        String storedHash = uriToHashMap.get(httpRequest.getRequestURI());
        if (storedHash == null) {
            // not yet found or request not valid, will try it
            return retrieveHashForRequestURIIfValidThenUpdateMapElseHandleRequest(httpRequest, httpResponse, chain);
        } else if (resourcesCanChange) {
            // we already have a hash but the resource can change, so we will try it
            String computedHash = retrieveHashForRequestURIIfValidThenUpdateMapElseHandleRequest(httpRequest, httpResponse, chain);
            if (storedHash.equals(computedHash)) {
                return storedHash;
            } else {
                // resource has changed, let's redirect to the new hash
                redirectToHashUrl(httpRequest, httpResponse, chain, computedHash);
                return null;
            }
        } else {
            return storedHash;
        }
    }

    private String retrieveHashForRequestURIIfValidThenUpdateMapElseHandleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException {
        ComputeHashHttpServletResponseWrapper responseWrapper = new ComputeHashHttpServletResponseWrapper(httpResponse);

        chain.doFilter(new IgnoringCacheHttpServletRequestWrapper(httpRequest), responseWrapper);

        if (BooleanUtils.isTrue(responseWrapper.requestOK)) {
            // the response wasn't an error, so we captured the output and computed the hash
            String hash = Base64Utils.convertFromBytesToBase64RFC4648(responseWrapper.hasher.hash().asBytes()).substring(0, 6); // we strip the last 2 "=" character that will still be there as the hash is 32bits, which makes 4 bytes, so 6 Base64 chars with 2 "=" padding at the ead
            uriToHashMap.put(httpRequest.getRequestURI(), hash);
            return hash;
        } else {
            // the response was an error, so it was already handled, return null
            return null;
        }
    }

    private static class IgnoringCacheHttpServletRequestWrapper extends HttpServletRequestWrapper {
        public IgnoringCacheHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if ("If-Modified-Since".equals(name) || "If-None-Match".equals(name)) {
                return null;
            } else {
                return super.getHeader(name);
            }
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            HashSet<String> headerNames = new HashSet<>(Collections.list(super.getHeaderNames()));
            headerNames.remove("If-Modified-Since");
            headerNames.remove("If-None-Match");
            return Collections.enumeration(headerNames);
        }

        @Override
        public long getDateHeader(String name) {
            if ("If-Modified-Since".equals(name)) {
                return -1;
            } else {
                return super.getDateHeader(name);
            }
        }
    }

    private static class ComputeHashHttpServletResponseWrapper extends HttpServletResponseWrapper {
        private final Hasher hasher = Hashing.murmur3_32().newHasher();
        private Boolean requestOK = null;

        public ComputeHashHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (requestOK == null || requestOK) {
                requestOK = true;
                return new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void write(int b) throws IOException {
                        hasher.putByte((byte) b);
                    }
                };
            } else {
                requestOK = false;
                return super.getOutputStream();
            }
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(getOutputStream());
        }

        @Override
        public void setStatus(int sc) {
            if (200 <= sc && sc < 300) {
                requestOK = Boolean.TRUE;
            } else {
                super.setStatus(sc);
            }
        }

        @Override
        public void setContentLength(int len) {
            if (BooleanUtils.isNotTrue(requestOK)) {
                super.setContentLength(len);
            }
        }

        @Override
        public void setContentLengthLong(long len) {
            if (BooleanUtils.isNotTrue(requestOK)) {
                super.setContentLengthLong(len);
            }
        }
    }


    private void redirectToHashUrl(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain, String computedHash) throws IOException {
        httpResponse.sendRedirect(httpRequest.getRequestURI() + "?v=" + computedHash);
    }


    @Override
    public void destroy() {
        // Do nothing
    }
}
