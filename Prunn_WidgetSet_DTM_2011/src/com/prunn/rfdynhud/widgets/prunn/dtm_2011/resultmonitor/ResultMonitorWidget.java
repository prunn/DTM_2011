package com.prunn.rfdynhud.widgets.prunn.dtm_2011.resultmonitor;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionType;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class ResultMonitorWidget extends Widget
{
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsName2 = null;
    private DrawnString[] dsTeam = null;
    private DrawnString[] dsTime = null;
    private DrawnString[] dsTime2 = null;
    private DrawnString dsTrack = null;
    private DrawnString dsSession = null;
    private DrawnString dsDTMFoot = null;
    
    private final ImagePropertyWithTexture imgTrack = new ImagePropertyWithTexture( "imgTrack", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgSession = new ImagePropertyWithTexture( "imgSession", "prunn/DTM/black_alpha.png" );
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgDTM = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/DTM/dtm_logo.png" );
    private final ImagePropertyWithTexture imgDTMFoot = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/DTM/black.png" );
    
    protected final FontProperty DTMFont = new FontProperty("Main Font", PrunnWidgetSetDTM_2011.DTM_FONT_NAME);
    private final FontProperty posFont = new FontProperty("positionFont", PrunnWidgetSetDTM_2011.POS_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty( "fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME );
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME );
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetDTM_2011.FONT_COLOR3_NAME );
    
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 10 );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", -2);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    
    private IntValue[] positions = null;
    private StringValue[] driverNames = null;
    private StringValue[] driverTeam = null;
    private FloatValue[] gaps = null;
    StandardTLCGenerator gen = new StandardTLCGenerator();
    
    
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
    
    private void initValues()
    {
        int maxNumItems = numVeh.getValue();
        
        if ( ( positions != null ) && ( positions.length == maxNumItems ) )
            return;
        
        gaps = new FloatValue[maxNumItems];
        positions = new IntValue[maxNumItems];
        driverNames = new StringValue[maxNumItems];
        driverTeam = new StringValue[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        { 
            positions[i] = new IntValue();
            driverNames[i] = new StringValue();
            driverTeam[i] = new StringValue();
            gaps[i] = new FloatValue();
        }
        
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int fhPos = TextureImage2D.getStringHeight( "0%C", posFont );
        int rowHeight = height / (maxNumItems + 2);
        
        imgDTM.updateSize( width*24/100, rowHeight, isEditorMode );
        imgDTMFoot.updateSize( width, rowHeight/2, isEditorMode );
        imgTrack.updateSize( width*76/100, rowHeight/2, isEditorMode );
        imgSession.updateSize( width*76/100, rowHeight/2, isEditorMode );
        imgPos.updateSize( width*8/100, rowHeight*96/100, isEditorMode );
        imgName.updateSize( width*71/100, rowHeight*50/100, isEditorMode );
        imgTeam.updateSize( width*71/100, rowHeight*47/100, isEditorMode );
        imgTime.updateSize( width*20/100, rowHeight*96/100, isEditorMode );
        Color whiteFontColor = fontColor2.getColor();
        
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsName2 = new DrawnString[maxNumItems];
        dsTeam = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        dsTime2 = new DrawnString[maxNumItems];
        
        
        int top = ( rowHeight - fh ) / 2;
        
        dsTrack = drawnStringFactory.newDrawnString( "dsTrack", width*3/100, top + fontyoffset.getValue() - rowHeight/4, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        dsSession = drawnStringFactory.newDrawnString( "dsSession", width*5/100, top + fontyoffset.getValue() + rowHeight/4, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        top += rowHeight;
        top += height / (maxNumItems + 20);
        //top += rowHeight;
        
        for(int i=0;i < maxNumItems;i++)
        {
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*6/100 + fontxposoffset.getValue(), top - fhPos/4 + fontyoffset.getValue(), Alignment.CENTER, false, posFont.getFont(), isFontAntiAliased(), fontColor1.getColor() );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*12/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue() - rowHeight/4, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), whiteFontColor );
            dsName2[i] = drawnStringFactory.newDrawnString( "dsName", width*12/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue() - rowHeight/4, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsTeam[i] = drawnStringFactory.newDrawnString( "dsTeam", width*12/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue() + rowHeight/4, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor1.getColor() );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime",  width*97/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue() - rowHeight/4, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), whiteFontColor );
            dsTime2[i] = drawnStringFactory.newDrawnString( "dsTime",  width*97/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue() + rowHeight/4, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), whiteFontColor );
            
            top += rowHeight;
        }
        
        dsDTMFoot = drawnStringFactory.newDrawnString( "dsSession", width*90/100, height + fontyoffset.getValue() - fh/2 - rowHeight/4, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor() );
        
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        initValues();
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
            
            if(vsi != null)
            {
                positions[i].update( vsi.getPlace( false ) );
                driverNames[i].update( vsi.getDriverName());
                
                if(vsi.getVehicleInfo() != null)
                    driverTeam[i].update( gen.generateShortTeamNames( vsi.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ));
                else
                    driverTeam[i].update( vsi.getVehicleClass());
                
                if(scoringInfo.getSessionType() != SessionType.RACE1)
                    gaps[i].update(vsi.getBestLapTime());
                else
                    gaps[i].update(vsi.getNumPitstopsMade());
                    
                    
                
            }
        }
        return true;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int maxNumItems = numVeh.getValue();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        int rowHeight = height / (maxNumItems + 2);
        
        texture.clear( imgTrack.getTexture(), offsetX, offsetY, false, null );
        texture.clear( imgSession.getTexture(), offsetX, offsetY+rowHeight/2, false, null );
        texture.clear( imgDTM.getTexture(), offsetX + width*76/100, offsetY, false, null );
        
        for(int i=0;i < drawncars;i++)
        {
            texture.clear( imgPos.getTexture(), offsetX + width*2/100, offsetY+rowHeight*(i+2) - height/(maxNumItems + 8), false, null );
            texture.clear( imgName.getTexture(), offsetX + width*10/100, offsetY+rowHeight*(i+2) - height/(maxNumItems + 8), false, null );
            texture.clear( imgTeam.getTexture(), offsetX + width*10/100, offsetY+rowHeight*(i+2) + rowHeight/2 - height/(maxNumItems + 8), false, null );
            texture.clear( imgTime.getTexture(), offsetX + width*79/100, offsetY+rowHeight*(i+2) - height/(maxNumItems + 8), false, null );
        }
        
        texture.clear( imgDTMFoot.getTexture(), offsetX, offsetY + height - rowHeight/2, false, null );
        
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        String SessionName;
        //one time for leader
        
        if ( needsCompleteRedraw || clock.c())
        {
            switch(scoringInfo.getSessionType())
            {
                case RACE1: case RACE2: case RACE3: case RACE4:
                    SessionName = "Race";
                    break;
                case QUALIFYING1: case QUALIFYING2: case QUALIFYING3:  case QUALIFYING4:
                    SessionName = "Qualifying";
                    break;
                case PRACTICE1:
                    SessionName = "Practice 1";
                    break;
                case PRACTICE2:
                    SessionName = "Practice 2";
                    break;
                case PRACTICE3:
                    SessionName = "Practice 3";
                    break;
                case PRACTICE4:
                    SessionName = "Practice 4";
                    break;
                case TEST_DAY:
                    SessionName = "Test";
                    break;
                case WARMUP:
                    SessionName = "Warmup";
                    break;
                default:
                    SessionName = "";
                    break;
                        
            }
            //" Session Classification"
            dsTrack.draw( offsetX, offsetY, gameData.getTrackInfo().getTrackName(), texture);
            dsSession.draw( offsetX, offsetY, SessionName + " " + " Session Classification", texture);
            dsDTMFoot.draw( offsetX, offsetY, "www.dtm.com", texture);
            
            dsPos[0].draw( offsetX, offsetY, positions[0].getValueAsString(), texture );
            dsName[0].draw( offsetX, offsetY, gen.generateThreeLetterCode2( driverNames[0].getValue(), gameData.getFileSystem().getConfigFolder() )  + " | " + driverNames[0].getValue(), texture );
            dsName2[0].draw( offsetX, offsetY, gen.generateThreeLetterCode2( driverNames[0].getValue(), gameData.getFileSystem().getConfigFolder() ), texture );
            dsTeam[0].draw( offsetX, offsetY, driverTeam[0].getValue(), texture );
            
            if(scoringInfo.getSessionType() == SessionType.RACE1 )
            {
                String stops = ( scoringInfo.getLeadersVehicleScoringInfo().getNumPitstopsMade() > 1 ) ? " Stops" : " Stop";
                dsTime[0].draw( offsetX, offsetY, scoringInfo.getLeadersVehicleScoringInfo().getNumPitstopsMade() + stops, texture);
            }
            else
                if(gaps[0].isValid())
                    dsTime[0].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[0].getValue() ) , texture);
                else
                    dsTime[0].draw( offsetX, offsetY, "", texture);
        
        
            // the other guys"No Time Set"
            for(int i=1;i < drawncars;i++)
            { 
                if ( needsCompleteRedraw || clock.c() )
                {
                    dsPos[i].draw( offsetX, offsetY, positions[i].getValueAsString(), texture );
                    dsName[i].draw( offsetX, offsetY, gen.generateThreeLetterCode2( driverNames[i].getValue(), gameData.getFileSystem().getConfigFolder() )  + " | " + driverNames[i].getValue() , texture );  
                    dsName2[i].draw( offsetX, offsetY, gen.generateThreeLetterCode2( driverNames[i].getValue(), gameData.getFileSystem().getConfigFolder() ) , texture );  
                    dsTeam[i].draw( offsetX, offsetY, driverTeam[i].getValue(), texture );
                    if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() == FinishStatus.DQ)
                        dsTime[i].draw( offsetX, offsetY, "DQ", texture);
                    else
                        if(scoringInfo.getSessionType() == SessionType.RACE1 )
                        {
                            if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() == FinishStatus.DNF)
                                dsTime[i].draw( offsetX, offsetY, "DNF", texture); 
                            else
                            {
                                String stops = ( scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() > 1 ) ? " Stops" : " Stop";
                                dsTime[i].draw( offsetX, offsetY, scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() + stops, texture);
                            }
                        }
                        else
                            if(gaps[i].isValid())
                            {
                                dsTime[i].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[i].getValue() ), texture);
                                dsTime2[i].draw( offsetX, offsetY,"+" + TimingUtil.getTimeAsLaptimeString(Math.abs( gaps[i].getValue() - gaps[0].getValue() )) , texture);
                            }
                                
                                    
                 }
                
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( DTMFont, "" );
        writer.writeProperty( posFont, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( DTMFont ) );
        else if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( DTMFont );
        propsCont.addProperty( posFont );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor3 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
        propsCont.addProperty( numVeh );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
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
    
    public ResultMonitorWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011, 57f, 75.6f );
        
        getBackgroundProperty().setColorValue( "#00000057" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME );
    }
}
