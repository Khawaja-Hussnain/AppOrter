package com.khtech.apporter;

import android.view.View;

//This is interface for items click

public interface ItemClickListner {

    void onClick(View view, int position, boolean isLongClick);

}
