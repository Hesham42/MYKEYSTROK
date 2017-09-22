package com.example.heshammostafa.mykeystrok.DataBase;

import com.example.heshammostafa.mykeystrok.Event.BioEvent;

import java.nio.*;
import java.util.List;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */

public interface BioEventConsumer {
    void onEventsReceived(java.nio.Buffer buffer, List<? extends BioEvent> events);
    void onSessionEnd(java.nio.Buffer buffer);

    void onEventsReceived(Buffer buffer, List<? extends BioEvent> eventList);

    void onSessionEnd(Buffer buffer);
}
