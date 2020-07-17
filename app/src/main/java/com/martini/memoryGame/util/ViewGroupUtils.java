package com.martini.memoryGame.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ViewGroupUtils {
    public static List<View> getViewsByTag(View root, String tag) {
        List<View> result = new LinkedList<View>();
        if (root instanceof ViewGroup) {
            final int childCount = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < childCount; i++) {
                result.addAll(getViewsByTag(((ViewGroup) root).getChildAt(i), tag));
            }
        }
        final Object rootTag = root.getTag();
        // handle null tags, code from Guava's Objects.equal
        if (Objects.equals(tag, rootTag)) {
            result.add(root);
        }
        return result;
    }
}