#include "arithmetic_coding.h"
#include "huffman_coding.h"


typedef struct huffmanNode {
    bool    Leaf,
            Used;
    double  Probability;
} HuffmanNode;

typedef struct huffmanNodes {
    HuffmanNode    **NodesArray;
    int             RealSize,
                    Size;
} HuffmanNodes;


double          countAvarageLength  (CodeData*);
HuffmanNode*    addNewNode          (HuffmanNodes*);
HuffmanNodes*   newNodes            (int);


double countHCAvarageLength (CodeData *data) {
    range           AvarangeLength = 0.0,
                    Father = 0.0,
                    Min1,
                    Min2,
                    Probability;
    HuffmanNode     *Node;
    HuffmanNodes    *Nodes = newNodes (CharsNo);
    int             FirstMin,
                    SecondMin,
                    i;

    for (i = 0; i < CharsNo; i++)
        if ((Probability = data->Possibilities [i+1] - data->Possibilities [i]) > 0.0) {
            Node = addNewNode (Nodes);
            Node->Probability = Probability;
            Node->Leaf = true;
            Node->Used = false;
        }

    while (Father <= 0.99) {
        Min1 = 1.1;
        Min2 = 1.1;
        FirstMin = 0;
        SecondMin = 0;

        for (i = 0; i < Nodes->Size; i++) {
            Node = Nodes->NodesArray [i];
            if (Node->Probability < Min1 && !Node->Used) {
                Min2 = Min1;
                Min1 = Node->Probability;
                SecondMin = FirstMin;
                FirstMin = i;
            } else if (Node->Probability < Min2 && !Node->Used) {
                Min2 = Node->Probability;
                SecondMin = i;
            }
        }

        Father = Min1 + Min2;
        Node = addNewNode (Nodes);
        Node->Probability = Father;
        Node->Used = false;
        Node->Leaf = false;
        Nodes->NodesArray [FirstMin]->Used = true;
        Nodes->NodesArray [SecondMin]->Used = true;
    }

    for (i = 0; i < Nodes->Size; i++)
        if (!Nodes->NodesArray [i]->Leaf)
            AvarangeLength += Nodes->NodesArray [i]->Probability;

    return AvarangeLength;
}

HuffmanNode* addNewNode (HuffmanNodes *nodes) {
    HuffmanNode    **NewNodes;
    int             i,
                    Returned = nodes->Size;

    nodes->NodesArray [Returned] = (HuffmanNode*) malloc (sizeof (HuffmanNode));
    nodes->Size++;

    if (nodes->Size == nodes->RealSize) {
        NewNodes = (HuffmanNode**) malloc (nodes->RealSize * sizeof (HuffmanNode*) * 2);
        for (i = 0; i < nodes->Size; i++)
            NewNodes [i] = nodes->NodesArray [i];
        nodes->NodesArray = NewNodes;
        nodes->RealSize *= 2;
    }

    return nodes->NodesArray [Returned];
}

HuffmanNodes* newNodes (int howMany) {
    HuffmanNodes   *Nodes = (HuffmanNodes*) malloc (sizeof (HuffmanNodes));
    if (Nodes == NULL)
        return NULL;

    if ((Nodes->NodesArray = (HuffmanNode**) malloc (howMany * sizeof (HuffmanNode*))) != NULL) {
        Nodes->Size = 0;
        Nodes->RealSize = howMany;
    } else {
        free (Nodes);
        return NULL;
    }

    return Nodes;
}
