import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'shorter' })
export class ShorterPipe implements PipeTransform {
  transform(str: string, maxLength: number): string {
    if (!str || str.length <= maxLength) {
      return str;
    } else if (str) {
      return str.substr(0, maxLength) + '…';
    } else {
      return str;
    }
  }
}
