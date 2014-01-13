package it.tapion.cupidosmsscheduler;

public class SMS {

    // attributi privati
    private int _id;
    private String _text;
    private int _sent;


    // costruttori
    public SMS() {

    }

    public SMS(String text) {
        this._text = text;
        this._sent = 0;
    }

    // getter e setter
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public int get_sent() {
        return _sent;
    }

    public void set_sent(int _sent) {
        this._sent = _sent;
    }

    public String toString() {
        return this.get_text();
    }

}
