# SwinGifts - Let's swing gifts between us!

This application aims to manage gifts wishlists between friends and to be able to designate one friend to offer a gift to for a specific event, a sort of "circle of gift".

# Installing SwinGifts

You should first [install Docker](https://docs.docker.com/engine/installation/), and have bash available (on Windows, install [Git](https://git-scm.com/download/win), and you will have such a terminal using "Git Bash").

Now you can compile & run SwinGifts using the following command:
```bash
./run_with_docker.sh --base-dir ./docker-data
```

More information on how to use this script can be found by running `./run_with_docker.sh --help`.

# TODOs

 * Translate all the application thanks to [angular-translate](https://github.com/angular-translate/angular-translate);
 * Allow the admin of an event to make a user subscribe for the circle gift;