package com.prunn.rfdynhud.widgets.prunn.dtm_2011.tracktemps;

import java.awt.Font;
import java.io.IOException;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class TrackTempsWidget extends Widget
{
    private DrawnString dsAmbient = null;
    private DrawnString dsAmbientTemp = null;
    private DrawnString dsTrack = null;
    private DrawnString dsTrackTemp = null;
    
    private IntValue AmbientTemp = new IntValue();
    private IntValue TrackTemp = new IntValue();
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgData = new ImagePropertyWithTexture( "imgName", "prunn/DTM/black_alpha.png" );
    
    protected final FontProperty DTMFont = new FontProperty("Main Font", PrunnWidgetSetDTM_2011.DTM_FONT_NAME);
    protected final ColorProperty WhiteFontColor = new ColorProperty("fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME);
    private BooleanProperty ShowAmbiant = new BooleanProperty("Show Ambiant", "ShowAmbiant", true);
    
    
    
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int fh = TextureImage2D.getStringHeight( "0%C", DTMFont );
        
        imgName.updateSize( width*60/100, height*48/100, isEditorMode );
        imgData.updateSize( width*40/100, height*48/100, isEditorMode );
        
        dsAmbient = drawnStringFactory.newDrawnString( "dsAmbient", width*96/100, height*3/4 - fh/2 + fontyoffset.getValue(), Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsAmbientTemp = drawnStringFactory.newDrawnString( "dsAmbientTemp", width*7/100, height*3/4 - fh/2 + fontyoffset.getValue(), Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "°C");
        
        dsTrack = drawnStringFactory.newDrawnString( "dsTrack", width*96/100, height/4 - fh/2 + fontyoffset.getValue(), Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "" );
        dsTrackTemp = drawnStringFactory.newDrawnString( "dsTrackTemp", width*7/100, height/4 - fh/2 + fontyoffset.getValue(), Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), WhiteFontColor.getColor(), null, "°C");
        
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        texture.clear( imgData.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgName.getTexture(), offsetX + width*40/100, offsetY, false, null );
        
        if(ShowAmbiant.getValue())
        {
            texture.clear( imgData.getTexture(), offsetX, offsetY + height/2, false, null );
            texture.clear( imgName.getTexture(), offsetX + width*40/100, offsetY + height/2, false, null );
        }
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        
        AmbientTemp.update((int)Math.floor(gameData.getWeatherInfo().getAmbientTemperature()));
        TrackTemp.update((int)Math.floor(gameData.getWeatherInfo().getTrackTemperature()));
        
        if ( ShowAmbiant.getValue() && (needsCompleteRedraw || AmbientTemp.hasChanged()) )
        {
            dsAmbient.draw( offsetX, offsetY, "AIR", texture );
            dsAmbientTemp.draw( offsetX, offsetY, AmbientTemp.getValueAsString(), texture);
        }
        if ( needsCompleteRedraw || TrackTemp.hasChanged())
        {
            dsTrack.draw( offsetX, offsetY, "TRACK", texture );
            dsTrackTemp.draw( offsetX, offsetY, TrackTemp.getValueAsString(), texture);
        }
         
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( DTMFont, "" );
        writer.writeProperty( WhiteFontColor, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( ShowAmbiant, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( DTMFont ) );
        else if ( loader.loadProperty( WhiteFontColor ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( ShowAmbiant ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( DTMFont );
        propsCont.addProperty( WhiteFontColor );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( ShowAmbiant );
    }
    @Override
    protected boolean canHaveBorder()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
        
    }
    
    public TrackTempsWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011, 20f, 8f );
        getBackgroundProperty().setColorValue( "#00000000" );
    }
    
}
