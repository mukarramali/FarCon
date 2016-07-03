package prashushi.farcon;

/**
 * Created by Dell User on 6/27/2016.
 */
public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "myriad.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "italics.ttf");
        System.out.println("myriad text set");
      //  FontsOverride.setDefaultFont(this, "SERIF", "MyFontAsset3.ttf");
      //  FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");
    }
}