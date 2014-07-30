public class Container {
    private Element[] Store;

    private int[] Array;

    private int Comparisions;

    private boolean DisplayFull;

    private boolean Show;

    public Container (int[] array, boolean show, boolean displayFull) {
        int i;

        this.Array = array;

        this.Comparisions = 0;

        this.DisplayFull = displayFull;

        this.Show = show;

        this.Store = new Element[array.length];

        for (i = 0; i < array.length; i++)
            this.Store [i] = new Element (i+1, array [i]);
    }

    public void addComparision () {
        this.addComparision (1);
    }

    public void addComparision (int amount) {
        this.Comparisions += amount;
    }

    public boolean differ (int key1, int key2) {
        this.Comparisions++;
        return (boolean) (this.get (key1).getValue()!= this.get (key2).getValue());
    }

    public void display () {
        this.display (false);
    }

    public void display (boolean show) {
        int i, j, Max;

        if (!this.Show && !show)
            return;

        Max = 0;
        for (i = 1; i <= this.getSize (); i++)
            if (this.get (i).getValue () > Max)
                Max = this.get (i).getValue ();

        if (this.DisplayFull) {
            for (j = Max; j > 0; j--) {
                for (i = 1; i <= this.getSize (); i++) {
                    if (this.get (i).getValue () >= j)
                        System.out.print ("@");
                    else
                        System.out.print (" ");
                }
                System.out.print ("\n");
            }
            for (i = 1; i <= this.getSize (); i++)
                System.out.print ("-");
        } else {
            for (i = 1; i <= this.getSize (); i++)
                System.out.print (this.get (i).getValue () + " ");
        }
        System.out.print ("\n\n");
    }

    public void displayStats () {
        int Max = 0, Moved = 0, i;

        for (i = 1; i <= this.getSize (); i++) {
            Moved += this.get (i).moved ();
            if (this.get(i). moved () > Max)
                Max = this.get (i).moved ();
        }

        System.out.println ("Comparisions: " + this.Comparisions + ", Items moved " + Moved + " times, Most often moved item(s):");

        for (i = 1; i <= this.getSize (); i++)
            if (this.get (i).moved () == Max)
                System.out.println ("Original index: " +
                                    this.get (i).originallyAt () +
                                    ", Current index: " +
                                    this.get (i).currentlyAt () +
                                    ", Value: " +
                                    this.get (i). getValue () +
                                    ", Moved " +
                                    Max +
                                    " times");
    }

    public Element get (int key) {
        return this.Store [key-1];
    }

    public int[] getArray () {
        this.refreshArray ();

        return this.Array;
    }

    public int getComparisions () {
        return this.Comparisions;
    }

    public int getSize () {
        return this.Store.length;
    }

    public boolean greater (int key1, int key2) {
        this.Comparisions++;
        return (boolean) (this.get (key1).getValue() > this.get (key2).getValue());
    }

    public boolean greaterEqual (int key1, int key2) {
        this.Comparisions++;
        return (boolean) (this.get (key1).getValue() >= this.get (key2).getValue());
    }

    public boolean lesser (int key1, int key2) {
        this.Comparisions++;
        return (boolean) (this.get (key1).getValue() < this.get (key2).getValue());
    }

    public boolean lesserEqual (int key1, int key2) {
        this.Comparisions++;
        return (boolean) (this.get (key1).getValue() <= this.get (key2).getValue());
    }

    public void refreshArray () {
        int i;

        for (i = 0; i < this.getSize (); i++)
            this.Array [i] = this.Store [i].getValue ();
    }

    public void set (int key, Element value) {
        this.Store [key-1] = value;
        this.get (key).movedTo (key);
    }

    public void swap (int key1, int key2) {
        Element Temp;

        Temp = this.get (key1);
        this.set (key1, this.get (key2));
        this.set (key2, Temp);
    }
}
