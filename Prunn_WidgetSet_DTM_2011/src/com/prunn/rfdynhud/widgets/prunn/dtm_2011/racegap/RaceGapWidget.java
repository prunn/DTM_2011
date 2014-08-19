package com.prunn.rfdynhud.widgets.prunn.dtm_2011.racegap;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;
import com.prunn.rfdynhud.widgets.prunn.dtm_2011.raceinfos.RaceInfosWidget;

import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
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
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceGapWidget extends Widget
{
    private DrawnString dsPos = null;
    private DrawnString dsPos2 = null;
    private DrawnString dsName = null;
    private DrawnString dsName2 = null;
    private DrawnString dsTime = null;
    
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/DTM/black.png" );

    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    protected final IntProperty frequency = new IntProperty("appearence frequency", "frequency", 3);
    protected final FontProperty DTMFont = new FontProperty("Main Font", PrunnWidgetSetDTM_2011.DTM_FONT_NAME);
    protected final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME);
    protected final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME);
    protected final ColorProperty fontColor4 = new ColorProperty("fontColor4", PrunnWidgetSetDTM_2011.FONT_COLOR4_NAME);
    private BooleanProperty uppercasename = new BooleanProperty("uppercase name",true); 
    
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private final IntValue CurrentSector = new IntValue();
    private String  name, name2;
    private int place, place2;
    private String gap;
    //private IntValue cpos = new IntValue();
    StandardTLCGenerator gen = new StandardTLCGenerator();
    
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
    }
    private static final String getTimeAsGapString2( float gap )
    {
        return ( "" + TimingUtil.getTimeAsLaptimeString( gap ) );
    }
    private void fillvsis(ScoringInfo scoringInfo)
    {
        VehicleScoringInfo viewedvsi = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo vsi1;
        VehicleScoringInfo vsi2;
        float GapFront = 1000.000f;
        float GapBehind = 0.000f;
        int LapFront = 1000;
        int LapBehind = 0;
        
        if(viewedvsi.getNextInFront( false ) != null)
        {
            GapFront = Math.abs(viewedvsi.getTimeBehindNextInFront( false ));
            LapFront = viewedvsi.getLapsBehindNextInFront( false );
        }
        if(viewedvsi.getNextBehind( false ) != null)
        {
            GapBehind = Math.abs( viewedvsi.getNextBehind( false ).getTimeBehindNextInFront( false ));
            LapBehind = viewedvsi.getNextBehind( false ).getLapsBehindNextInFront( false );
        }
        
        if(viewedvsi.getNextBehind( false ) == null || GapFront < GapBehind || LapFront < LapBehind)
        {
            vsi1 = viewedvsi.getNextInFront( false );
            vsi2 = viewedvsi;
            if(LapFront == 0)
                gap = getTimeAsGapString2(GapFront);
            else
            {
                String laps = ( LapFront > 1 ) ? " Laps" : " Lap";
                gap = "" + LapFront + laps;
            }
        }
        else
        {
            vsi1 = viewedvsi;
            vsi2 = viewedvsi.getNextBehind( false );
            if(LapBehind == 0)
                gap = getTimeAsGapString2(GapBehind);
            else
            {
                String laps = ( LapBehind > 1 ) ? " Laps" : " Lap";
                gap = "" + LapBehind + laps;
            }
        }
        
        place = vsi1.getPlace(false);
        name = gen.ShortNameWTCC( vsi1.getDriverNameShort());
        place2 = vsi2.getPlace(false);
        name2 = gen.ShortNameWTCC( vsi2.getDriverNameShort());
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
        
        imgName.updateSize( width*61/100, height*47/100, isEditorMode );
        imgPos.updateSize( width*10/100, height*47/100, isEditorMode );
        imgTime.updateSize( width*29/100, height*97/100, isEditorMode );
        
        int fh = TextureImage2D.getStringHeight( "0yI", DTMFont );
        int top1 = height/4 - fh/2 + fontyoffset.getValue() - height*3/100;
        int top2 = height*3/4 - fh/2 + fontyoffset.getValue() - height*3/100;
        
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*5/100, top1, Alignment.CENTER, false, DTMFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        dsName = drawnStringFactory.newDrawnString( "dsName", width*12/100, top1, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width*95/100, top1, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor4.getColor(), null, "" );
        dsPos2 = drawnStringFactory.newDrawnString( "dsPos2", width*5/100, top2, Alignment.CENTER, false, DTMFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        dsName2 = drawnStringFactory.newDrawnString( "dsName2", width*12/100, top2, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
         
        CurrentSector.update(scoringInfo.getViewedVehicleScoringInfo().getSector());
        
        if(isEditorMode)
        {
            fillvsis(scoringInfo);
            return true;
        }
        
        if(RaceInfosWidget.visible() || scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() < 1 || scoringInfo.getViewedVehicleScoringInfo().getFinishStatus().isFinished())
        {
            isvisible = false;
            return false;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd)
        {
            isvisible = true;
            return true;
        }
            
        
        if( CurrentSector.hasChanged() && scoringInfo.getNumVehicles() > 1)
        {
            if( (int)(Math.random()*frequency.getValue()) == 0 )
            {
                //cpos.update(scoringInfo.getViewedVehicleScoringInfo().getPlace( false ));
                if(!isEditorMode)
                    forceCompleteRedraw( true );
                fillvsis(scoringInfo);
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
                isvisible = true;
                return true;
            }
        }
        isvisible = false;
        return false;
    		
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY, false, null );
        texture.clear( imgName.getTexture(), offsetX + imgPos.getTexture().getWidth(), offsetY + height/2, false, null );
        texture.clear( imgPos.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgPos.getTexture(), offsetX, offsetY + height/2, false, null );
        texture.clear( imgTime.getTexture(), offsetX + imgName.getTexture().getWidth() + imgPos.getTexture().getWidth(), offsetY, false, null );
            
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw  )
        {
            dsPos.draw( offsetX, offsetY, Integer.toString( place ), texture );
            
            if( uppercasename.getValue() )
                dsName.draw( offsetX, offsetY, name.toUpperCase(), texture );
            else
                dsName.draw( offsetX, offsetY, name, texture );
            
            dsTime.draw( offsetX, offsetY, gap , texture);
        	dsPos2.draw( offsetX, offsetY, Integer.toString( place2 ), texture );
        	if( uppercasename.getValue() )
        	    dsName2.draw( offsetX, offsetY, name2.toUpperCase(), texture );
        	else
        	    dsName2.draw( offsetX, offsetY, name2, texture );
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( DTMFont, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor4, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty(frequency, "");
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( uppercasename, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( DTMFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor4 ) );
        else if( loader.loadProperty(visibleTime));
        else if( loader.loadProperty(frequency));
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( uppercasename ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( DTMFont );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor4 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty(frequency);

        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( uppercasename );
        
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
    
    public RaceGapWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011_Race, 40.0f, 8.8f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 15);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
        
    }
    
}
