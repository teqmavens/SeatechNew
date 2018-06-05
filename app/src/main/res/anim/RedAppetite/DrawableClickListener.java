package com.vadevelopment.RedAppetite;

/**
 * Created by vibrantappz on 10/14/2017.
 */

public interface DrawableClickListener {
    public static enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}

    ;

    public void onClick(DrawablePosition target);
}
