#!/bin/bash
(echo -n 'const char *file = "'; cat model.3ds | hexdump -v -e '/1 "@x%02x"' | tr @ '\\'; echo '";') > model.h
