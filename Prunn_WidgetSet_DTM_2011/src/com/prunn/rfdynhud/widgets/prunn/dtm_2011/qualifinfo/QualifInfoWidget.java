package com.prunn.rfdynhud.widgets.prunn.dtm_2011.qualifinfo;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;
import com.prunn.rfdynhud.widgets.prunn.dtm_2011.qtime.QualTimeWidget;

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
import net.ctdp.rfdynhud.util.NumberUtil;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class QualifInfoWidget extends Widget
{
    
    
    private DrawnString dsPos = null;
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private DrawnString dsTime = null;
    private DrawnString dsGap = null;
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgPosOut = new ImagePropertyWithTexture( "imgPosOut", "prunn/DTM/red.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/DTM/black.png" );
    
    protected final FontProperty TeamFont = new FontProperty("Team Font", PrunnWidgetSetDTM_2011.TEAM_FONT_NAME);
    private final FontProperty posFont = new FontProperty("positionFont", PrunnWidgetSetDTM_2011.POS_FONT_NAME);
    protected final FontProperty DTMFont = new FontProperty("Main Font", PrunnWidgetSetDTM_2011.DTM_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME);
    private IntProperty knockout = new IntProperty("Knockout position", 8);
    private BooleanProperty uppercasename = new BooleanProperty("uppercase name",true); 
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private IntValue cveh = new IntValue();
    private BoolValue cpit = new BoolValue();
    StandardTLCGenerator gen = new StandardTLCGenerator();
    private StringValue team = new StringValue();
    private StringValue name = new StringValue();
    private StringValue pos = new StringValue();
    private StringValue gap = new StringValue();
    private StringValue time = new StringValue();
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
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
        
        team.update( "" );
        name.update( "" ); 
        pos.update( "" );
        gap.update( "" ); 
        time.update( "" ); 
        
        int rowHeight = height / 2;
        int fh = TextureImage2D.getStringHeight( "0", DTMFont );
        int fhPos = TextureImage2D.getStringHeight( "0", posFont );
        
        imgPos.updateSize( width*12/100, rowHeight*2, isEditorMode );
        imgPosOut.updateSize( width*12/100, rowHeight*2, isEditorMode );
        imgName.updateSize(width*65/100, rowHeight, isEditorMode );
        imgTeam.updateSize( width*65/100, rowHeight, isEditorMode );
        imgTime.updateSize( width*24/100, rowHeight, isEditorMode );
        
        int top1 = ( rowHeight - fh ) / 2;
        int top1Pos = ( height - fhPos ) / 2;
        int top2 = rowHeight + ( rowHeight - fh ) / 2;
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*6/100, top1Pos + fontyoffset.getValue(), Alignment.CENTER, false, posFont.getFont(), isFontAntiAliased(), fontColor1.getColor() );
        dsName = drawnStringFactory.newDrawnString( "dsName", width*14/100, top1 + fontyoffset.getValue(), Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", width*14/100, top2 + fontyoffset.getValue(), Alignment.LEFT, false, TeamFont.getFont(), isFontAntiAliased(), fontColor1.getColor() );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width*99/100, top1 + fontyoffset.getValue(), Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        dsGap = drawnStringFactory.newDrawnString( "dsGap", width*99/100, top2 + fontyoffset.getValue(), Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        
    }
    
    @Override
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        cveh.update(scoringInfo.getViewedVehicleScoringInfo().getDriverId());
        cpit.update(scoringInfo.getViewedVehicleScoringInfo().isInPits());
        
        if(QualTimeWidget.visible())
            return false;
        
        if((cveh.hasChanged() || cpit.hasChanged()) && !isEditorMode)
        {
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd || cpit.getValue())
            return true;
        
        
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        int rowHeight = height / 2;
            
        texture.clear( imgName.getTexture(), offsetX + width*12/100, offsetY, false, null );
        texture.clear( imgTeam.getTexture(), offsetX + width*12/100, offsetY + height / 2, false, null );
        
        if( currentcarinfos.getFastestLaptime() != null && currentcarinfos.getFastestLaptime().getLapTime() > 0 )
        {  
            texture.clear( imgTime.getTexture(), offsetX + width*76/100, offsetY, false, null );
            texture.clear( imgTime.getTexture(), offsetX + width*76/100, offsetY + rowHeight, false, null );
                
            if(currentcarinfos.getPlace( false ) <= knockout.getValue())    
                texture.clear( imgPos.getTexture(), offsetX, offsetY, false, null );
            else
                texture.clear( imgPosOut.getTexture(), offsetX, offsetY, false, null );
            
        }   
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	
    	if ( needsCompleteRedraw )
        {
    	    VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
            
        	name.update( gen.ShortNameWTCC( currentcarinfos.getDriverNameShort() ) );
            pos.update( NumberUtil.formatFloat( currentcarinfos.getPlace(false), 0, true));
            team.update( gen.generateShortTeamNames( currentcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ));
                
        	
            if( currentcarinfos.getFastestLaptime() != null && currentcarinfos.getFastestLaptime().getLapTime() > 0 )
            {
                if(currentcarinfos.getPlace( false ) > 1)
                { 
                    time.update( TimingUtil.getTimeAsLaptimeString(currentcarinfos.getBestLapTime() ));
                    gap.update( "+ " +  TimingUtil.getTimeAsLaptimeString( currentcarinfos.getBestLapTime() - gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() ));
                }
                else
                {
                    time.update("");
                    gap.update( TimingUtil.getTimeAsLaptimeString(currentcarinfos.getBestLapTime()));
                }
                    
            }
            else
            {
                time.update("");
                gap.update("");
            }
            if( currentcarinfos.getFastestLaptime() != null && currentcarinfos.getFastestLaptime().getLapTime() > 0 )
                dsPos.draw( offsetX, offsetY, pos.getValue(), texture );
            //if(( clock.c() && name.hasChanged()) || isEditorMode )
            if( uppercasename.getValue() )
                dsName.draw( offsetX, offsetY, name.getValue().toUpperCase(), texture );
            else    
                dsName.draw( offsetX, offsetY, name.getValue(), texture );
            
            //if(( clock.c() && team.hasChanged()) || isEditorMode )
                dsTeam.draw( offsetX, offsetY, team.getValue(), texture );
            //if(( clock.c() && time.hasChanged()) || isEditorMode ) 
                dsTime.draw( offsetX, offsetY, time.getValue(), texture);
            //if(( clock.c() && gap.hasChanged()) || isEditorMode ), TireFontColor.getColor()
                dsGap.draw( offsetX, offsetY, gap.getValue(), texture );
            
                
        }
         
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( DTMFont, "" );
        writer.writeProperty( TeamFont, "" );
        writer.writeProperty( posFont, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty( knockout, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( uppercasename, "" );
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( DTMFont ) );
        else if ( loader.loadProperty( TeamFont ) );
        else if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if( loader.loadProperty(visibleTime));
        else if ( loader.loadProperty( knockout ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( uppercasename ) );
        
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( DTMFont );
        propsCont.addProperty( TeamFont );
        propsCont.addProperty( posFont );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty( knockout );
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
    
    public QualifInfoWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011, 49.0f, 8.3f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0x8000000000000000L;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
    }
    
}
