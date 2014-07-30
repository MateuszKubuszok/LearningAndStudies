#include "arithmetic_coding.h"

int main (int argc, char **argv) {
    if (argc >= 3) {
        CodeData* Result = acEncodeFile (argv [1], argv [2]);

        if (Result != NULL)
            displayInfo (Result);
    } else {
        printf ("Invalid arguments.\n");
    }

    return 0;
}
