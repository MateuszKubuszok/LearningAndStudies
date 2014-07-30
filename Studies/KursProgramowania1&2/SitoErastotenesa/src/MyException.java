class MyException extends Exception {
    String ErrorInfo;

    MyException (String info) {
        this.ErrorInfo = info;
    }

    public String toString () {
        return this.ErrorInfo;
    }
}
