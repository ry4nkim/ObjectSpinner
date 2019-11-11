package kr.ry4nkim.objectspinner;

import java.util.List;

public interface OnFilterFinishedListener<T extends ObjectSpinner.Delegate> {

    void onFilterFinished(List<T> results);
}