package org.loklak.android.wok;

import java.io.IOException;
import java.util.Random;

import org.loklak.objects.MessageEntry;

import processing.core.PApplet;
import processing.core.PFont;

public class Sketch extends PApplet {

  public static PFont font = null;
  public static final int FRAME_RATE = 12;
  private final static Random random = new Random(System.currentTimeMillis());
  public static StatusLine statusLine;

  int fontsize;
  int randomX = 25, randomY = 0;
  int voff = 0; // for smooth line drawing
  boolean showsplash = true;
  boolean wasWifiConnected = true;
  boolean acceptNonWifiConnection = false;
  Buttons buttons_missingwifi, buttons_harvesting, buttons_splash;

  public Sketch() {

    // create preferences
    String apphash = Preferences.getConfig(Preferences.Key.APPHASH, "");
    if (apphash.length() == 0) {
      apphash = "LW_" + Integer.toHexString(Math.abs(("A404" + System.currentTimeMillis()).hashCode()));
      Preferences.setConfig(Preferences.Key.APPHASH, apphash);
    }

    new HarvestService().start();
  }

  public static void main(String[] args) {
    PApplet.main(Sketch.class.getName());
  }

  @Override
  public void settings() {
    fullScreen();
    size(width, height, JAVA2D);

    //fontsize = Math.min(width, height) / 38; // computes to a font size of 20
    
    fontsize = 20;
    
                                             // for a 768 width
//    if (font == null)
//      font = new PFont();
//      font = createFont("DroidSansMono.ttf", fontsize * 4, true); // at a
                                                                  // height of
                                                                  // 20, this
                                                                  // font has
                                                                  // a width
                                                                  // of 12
    // with this settings, we have exactly space for 64 characters on a
    // horizontal-oriented phone
    statusLine = new StatusLine(this, fontsize * 3 / 2, 32, 180, 230);
  }

  @Override
  public void setup() {
    
//    frameRate(FRAME_RATE);

    // first message
    statusLine.show("warming up loklak wok", 4000);

    // buttons for the splash screen
    buttons_splash = new Buttons(this);
    Buttons.Button button_splash_startapp = buttons_splash.createButton();
    button_splash_startapp.setCenter(width / 2, 5 * height / 6).setWidth(fontsize * 9).setFontsize(fontsize * 3 / 2)
        .setOffText("PUSH", "TO", "START").setOnText("", "", "").setBorderWidth(8).setBorderColor(32, 180, 230)
        .setOnColor(16, 90, 115).setOffColor(0, 0, 0).setTextColor(255, 200, 41).setTransitionTime(300).setStatus(0);
    buttons_splash.addButton("startapp", button_splash_startapp);

    // buttons for the missing-wifi screen
    buttons_missingwifi = new Buttons(this);
    Buttons.Button button_missingwifi_unlock0 = (Buttons.Button) button_splash_startapp.clone();
    button_missingwifi_unlock0.setCenter(width / 4, 5 * height / 6).setOffText("PRESS", "TO", "UNLOCK").setOnText("",
        "UNLOCKED", "");
    Buttons.Button button_missingwifi_unlock1 = (Buttons.Button) button_missingwifi_unlock0.clone();
    button_missingwifi_unlock1.setCenter(width / 2, 5 * height / 6).setStatus(0);
    Buttons.Button button_missingwifi_unlock2 = (Buttons.Button) button_missingwifi_unlock0.clone();
    button_missingwifi_unlock2.setCenter(3 * width / 4, 5 * height / 6).setStatus(0);
    buttons_missingwifi.addButton("unlock0", button_missingwifi_unlock0);
    buttons_missingwifi.addButton("unlock1", button_missingwifi_unlock1);
    buttons_missingwifi.addButton("unlock2", button_missingwifi_unlock2);

    // buttons for the harvesting screen
    buttons_harvesting = new Buttons(this);
    Buttons.Button button_harvesting_terminate = (Buttons.Button) button_missingwifi_unlock0.clone();
    button_harvesting_terminate.setCenter(fontsize, fontsize).setFontsize(fontsize).setWidth(fontsize).setBorderWidth(3)
        .setOffText("", "X", "").setOnText("", "", "").setTextColor(32, 180, 230);
    // buttons_harvesting.addButton("terminate", terminate);
    Buttons.Button button_harvesting_switchtomissingwifi = (Buttons.Button) button_missingwifi_unlock0.clone();
    button_harvesting_switchtomissingwifi.setCenter(width - fontsize, fontsize).setFontsize(fontsize).setWidth(fontsize)
        .setBorderWidth(3).setOffText("", "O", "").setOnText("", "", "").setTextColor(32, 180, 230).invisible();
    // buttons_harvesting.addButton("offline", offline);

    // pre-calculation of shape data
    GraphicData.init(width, height, fontsize);
  }

  @Override
  public void draw() {

    if (font == null) {
      font = createFont("DroidSansMono.ttf", fontsize * 4, true);
    }
    
    // clean up broken fonts (may happen for unknown reason)
    if (frameCount % (30 * FRAME_RATE) == 2) {
      // the font is broken after some time, we don't know the reason. This
      // fixes it.
      font = createFont("DroidSansMono.ttf", fontsize * 2, true);
    }

    // make a background
    colorMode(RGB, 256);
    background(30, 40, 50);
    textAlign(LEFT, TOP);

    // draw a headline
    color_bright_stroke();
    strokeWeight(1);
    GraphicData.headline_outline.draw(this, 0, 2, randomX, randomY);
    int vpos = GraphicData.headline_outline.getMaxY() + fontsize;

    // draw status line
    color_bright_fill();
    textFont(font, fontsize);
    statusLine.setY(vpos);
    statusLine.draw();
    vpos += 2 * fontsize;

    // ==== SHOW THE HARVESTINGT INFOGRAPHICS ====

    // create blala if nothing else is there
    if (statusLine.getQueueSize() == 0) {
      switch (random.nextInt(5)) {
      case 0:
        if (Harvester.suggestionsOnBackend != 1000)
          statusLine.show("Pending Back-End Queries: " + Harvester.suggestionsOnBackend, 1000);
        break;
      case 1:
        if (Harvester.pushToBackendAccumulationTimeline.size() != 0)
          statusLine.show("Pending Messages for Storage: " + Harvester.pushToBackendAccumulationTimeline.size(), 1000);
        break;
      case 2:
        if (Harvester.displayMessages.size() != 0)
          statusLine.show("Pending Lines: " + Harvester.displayMessages.size(), 1000);
        break;
      case 3:
        statusLine.show("http://loklak.org", 2000);
        break;
      case 4:
        if (Harvester.contribution_message_count > 0)
          statusLine.show("Stored a total of " + Harvester.contribution_message_count + " messages", 1000);
      }
    }

    // draw statistics
    int w = min(width, height);
    int h = w / 4;
    int d = h / 3 * 4;
    int ccx = width / 2;
    int ccy = vpos + h / 2;

    color_bright_stroke();
    strokeWeight(4);
    noFill();
    arc(ccx, ccy, d, d, 3 * PI / 4, 5 * PI / 4);
    arc(ccx, ccy, d, d, 0, PI / 4);
    arc(ccx, ccy, d, d, 7 * PI / 4, 2 * PI);
    line(0, ccy, ccx - d / 2, ccy);
    line(ccx + d / 2, ccy, width, ccy);
    textAlign(CENTER, CENTER);

    textFont(font, fontsize);
    color_dark_fill();
    text("HARVESTED", ccx, ccy - fontsize - fontsize / 2);
    textFont(font, fontsize * 2);
    color_bright_fill();
    text(Harvester.contribution_message_count == -1 ? "none" : "" + Harvester.contribution_message_count, width / 2,
        vpos + h / 2);

    textFont(font, fontsize);
    // above left
    color_dark_fill();
    text("QUERIES IN BACKEND", (ccx - d / 2) / 2, ccy - 3 * fontsize);
    color_bright_fill();
    text(Harvester.suggestionsOnBackend, (ccx - d / 2) / 2, ccy - 2 * fontsize);
    // above right
    color_dark_fill();
    text("PENDING LINES", // "QUERIES IN LOKLAK WOK",
        ccx + d / 2 + (ccx - d / 2) / 2, ccy - 3 * fontsize);
    color_bright_fill();
    text(Harvester.displayMessages.size(), // Harvester.pendingQueries.size(),
        ccx + d / 2 + (ccx - d / 2) / 2, ccy - 2 * fontsize);
    // below left
    color_dark_fill();
    text("CONTEXT PENDING", (ccx - d / 2) / 2, ccy + 2 * fontsize);
    color_bright_fill();
    text(Harvester.pendingContext.size(), (ccx - d / 2) / 2, ccy + 3 * fontsize);
    // below right
    color_dark_fill();
    text("HARVESTED CONTEXT", ccx + d / 2 + (ccx - d / 2) / 2, ccy + 2 * fontsize);
    color_bright_fill();
    text(Harvester.harvestedContext.size(), ccx + d / 2 + (ccx - d / 2) / 2, ccy + 3 * fontsize);

    vpos += h; // jump to text start

    // draw messages
    textFont(font, fontsize);
    textAlign(LEFT, TOP);
    d = 0;
    for (MessageEntry me : Harvester.displayMessages) {
      if (vpos + voff > height)
        break;

      color_dark_fill();
      text(me.getCreatedAt().toString() + " from @" + me.getScreenName(), 5, vpos + voff);
      vpos += fontsize;

      color_bright_fill();
      text(me.getText(10000, ""), 5, vpos + voff);
      vpos += fontsize;
      d++;
    }
    if (voff <= 0) {
      Harvester.reduceDisplayMessages();
      voff += fontsize * 2;
    }
    int ex = Harvester.displayMessages.size() - d;
    voff -= Math.min(fontsize * 2, Math.max(1, ex > 0 ? ex / 2 : 0));

    randomX = (randomX + Harvester.displayMessages.size() - d) / 3;

    // at some time load data from the newtork
    if (frameCount % FRAME_RATE == 1) {
      // this.getActivity().runOnUiThread(new Runnable() {
      // public void run() {
      Harvester.harvest();
      // }
      // });
    }

    // draw the buttons (always at last to make them visible at all cost)
    buttons_harvesting.draw();

    // react on button status
    /*
     * if (buttons_harvesting.getStatus("offline") == 255) {
     * acceptNonWifiConnection = false;
     * buttons_harvesting.getButton("offline").setStatus(0,0); } if
     * (buttons_harvesting.getStatus("terminate") == 255) this.exit();
     */

    // Log.d("Main", "draw time: " + (System.currentTimeMillis() - start) +
    // "ms");
  }

  private void color_bright_stroke() {
    stroke(32, 180, 230);
  }

  private void color_bright_fill() {
    fill(255, 200, 41);
  }

  private void color_dark_fill() {
    fill(32, 180, 230);
  }

  @Override
  public void mousePressed() {
    if (showsplash) {
      buttons_splash.mousePressed(mouseX, mouseY);
    } else {
      buttons_missingwifi.mousePressed(mouseX, mouseY);
      buttons_harvesting.mousePressed(mouseX, mouseY);
    }
  }

  @Override
  public void mouseDragged() {
  }
}
