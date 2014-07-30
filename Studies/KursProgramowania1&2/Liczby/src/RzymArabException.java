class RzymArabException extends Exception {
    String ErrorInfo;

    RzymArabException (String info) {
        this.ErrorInfo = info;
    }

    public String toString () {
        return this.ErrorInfo;
    }
}
