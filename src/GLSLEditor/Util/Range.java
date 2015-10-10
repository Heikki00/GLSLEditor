package GLSLEditor.Util;

/**
 * Created by Heikki on 26.9.2015.
 */
public class Range {
    private int start, end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }


    public boolean inRange(int pos){
        return pos >= start && pos <= end;
    }

}
