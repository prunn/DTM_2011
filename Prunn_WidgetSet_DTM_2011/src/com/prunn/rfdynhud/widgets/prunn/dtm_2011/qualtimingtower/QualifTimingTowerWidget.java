package com.prunn.rfdynhud.widgets.prunn.dtm_2011.qualtimingtower;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
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
import net.ctdp.rfdynhud.util.StandingsTools;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StandingsView;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class QualifTimingTowerWidget extends Widget
{
    private TextureImage2D texMercedes = null;
    private TextureImage2D texAudi = null;
    private TextureImage2D texOpel = null;
    private TextureImage2D texBMW = null;
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private final ImagePropertyWithTexture imgFirst = new ImagePropertyWithTexture( "imgFirst", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgFirstNew = new ImagePropertyWithTexture( "imgFirstNew", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/black_alpha.png" );
    private final ImagePropertyWithTexture imgPosNew = new ImagePropertyWithTexture( "imgPosNew", "prunn/DTM/black_alpha.png" );
    private final ImagePropertyWithTexture imgPosKnockOut = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/DTM/red.png" );
    private final ImagePropertyWithTexture imgPosNewKnockOut = new ImagePropertyWithTexture( "imgPosNewKnockOut", "prunn/DTM/red.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgNameNew = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgMercedes = new ImagePropertyWithTexture( "imgMercedes", "prunn/DTM/mercedes.png" );
    private final ImagePropertyWithTexture imgAudi = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/audi.png" );
    private final ImagePropertyWithTexture imgOpel = new ImagePropertyWithTexture( "imgMercedes", "prunn/DTM/opel.png" );
    private final ImagePropertyWithTexture imgBMW = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/bmw.png" );
    private final ImagePropertyWithTexture imgPlus = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/plus.png" );
    
    protected final FontProperty DTMFont = new FontProperty("Main Font", PrunnWidgetSetDTM_2011.DTM_FONT_NAME);
    protected final FontProperty TeamFont = new FontProperty("Team Font", PrunnWidgetSetDTM_2011.TEAM_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME );
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetDTM_2011.FONT_COLOR3_NAME );
    private final ColorProperty fontColor4 = new ColorProperty( "fontColor4", PrunnWidgetSetDTM_2011.FONT_COLOR4_NAME );
    
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 14 );
    private IntProperty knockoutQual = new IntProperty("Knockout position Qual", 8);
    private IntProperty knockoutFP1 = new IntProperty("Knockout position FP1", 14);
    private IntProperty knockoutFP2 = new IntProperty("Knockout position FP2", 14);
    private IntProperty knockoutFP3 = new IntProperty("Knockout position FP3", 14);
    private IntProperty knockoutFP4 = new IntProperty("Knockout position FP4", 14);
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    
    private final DelayProperty visibleTime = new DelayProperty( "visibleTime", DelayProperty.DisplayUnits.SECONDS, 5 );
    private long visibleEnd = -1L;
    private long[] visibleEndArray;
    
    private VehicleScoringInfo[] vehicleScoringInfos;
    private IntValue[] positions = null;
    private final IntValue numValid = new IntValue();
    private StringValue[] driverNames = null;
    private FloatValue[] gaps = null;
    private BoolValue[] IsInPit = null;
    private BoolValue[] IsFinished = null;
    private int knockout;
    private int[] driverIDs = null;
    private boolean[] gapFlag = null;
    private boolean[] gapFlag2 = null;
    StandardTLCGenerator gen = new StandardTLCGenerator();
    private final IntValue inputShowTimes = new IntValue();
    private StringValue[] manufacturer = null;
    
    
   
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        visibleEnd = -1L;
        numValid.reset();
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
        gapFlag = new boolean[maxNumItems];
        gapFlag2 = new boolean[maxNumItems];
        positions = new IntValue[maxNumItems];
        driverNames = new StringValue[maxNumItems];
        IsInPit = new BoolValue[maxNumItems];
        IsFinished = new BoolValue[maxNumItems];
        driverIDs = new int[maxNumItems];
        visibleEndArray = new long[maxNumItems];
        vehicleScoringInfos = new VehicleScoringInfo[maxNumItems];
        manufacturer = new StringValue[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        { 
            IsInPit[i] = new BoolValue();
            IsFinished[i] = new BoolValue();
            positions[i] = new IntValue();
            driverNames[i] = new StringValue();
            manufacturer[i] = new StringValue("");
            gaps[i] = new FloatValue();
        }
        
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems;
        
        imgPosNew.updateSize( width*16/100, rowHeight*95/100, isEditorMode );
        imgFirst.updateSize( width*16/100, rowHeight*95/100, isEditorMode );
        imgPos.updateSize( width*16/100, rowHeight*95/100, isEditorMode );
        imgFirstNew.updateSize( width*16/100, rowHeight*95/100, isEditorMode );
        imgName.updateSize( width*42/100, rowHeight*95/100, isEditorMode );
        imgNameNew.updateSize( width*84/100, rowHeight*95/100, isEditorMode );
        imgPlus.updateSize( width*7/100, rowHeight*45/100, isEditorMode );
        
        imgPosKnockOut.updateSize( width*16/100, rowHeight*95/100, isEditorMode );
        imgPosNewKnockOut.updateSize( width*16/100, rowHeight*95/100, isEditorMode );
        texMercedes = imgMercedes.getImage().getScaledTextureImage( width*17/100, rowHeight*95/100, texMercedes, isEditorMode );
        texAudi = imgAudi.getImage().getScaledTextureImage( width*17/100, rowHeight*95/100, texAudi, isEditorMode );
        texOpel = imgOpel.getImage().getScaledTextureImage( width*17/100, rowHeight*95/100, texOpel, isEditorMode );
        texBMW = imgBMW.getImage().getScaledTextureImage( width*17/100, rowHeight*95/100, texBMW, isEditorMode );
        
        Color whiteFontColor = fontColor2.getColor();
        
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        {
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*8/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, TeamFont.getFont(), isFontAntiAliased(), fontColor1.getColor() );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*34/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), whiteFontColor );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width*98/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), whiteFontColor );
            top += rowHeight;
        }
        
        switch(gameData.getScoringInfo().getSessionType())
        {
            case QUALIFYING1: case QUALIFYING2: case QUALIFYING3: case QUALIFYING4:
                knockout = knockoutQual.getValue();
                break;
            case PRACTICE1:
                knockout = knockoutFP1.getValue();
                break;
            case PRACTICE2:
                knockout = knockoutFP2.getValue();
                break;
            case PRACTICE3:
                knockout = knockoutFP3.getValue();
                break;
            case PRACTICE4:
                knockout = knockoutFP4.getValue();
                break;
            default:
                knockout = 100;
                break;
        }
        if(isEditorMode)
            knockout = knockoutQual.getValue();
        
        
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        
        initValues();
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        VehicleScoringInfo  comparedVSI = scoringInfo.getViewedVehicleScoringInfo();
        
        if(inputShowTimes.hasChanged())
            forceCompleteRedraw( true ); 
        if(scoringInfo.getViewedVehicleScoringInfo().getBestLapTime() > 0)
        {
            if(scoringInfo.getViewedVehicleScoringInfo().getPlace( false ) > numVeh.getValue())
                comparedVSI = scoringInfo.getVehicleScoringInfo( scoringInfo.getViewedVehicleScoringInfo().getPlace( false ) - 5 );
            else
                comparedVSI = scoringInfo.getLeadersVehicleScoringInfo();
        
        }
        else
        {
            comparedVSI = scoringInfo.getLeadersVehicleScoringInfo();
            /*for(int i=drawncars-1; i >= 0; i--)
            {
                if(scoringInfo.getVehicleScoringInfo( i ).getBestLapTime() > 0)
                {
                    comparedVSI = scoringInfo.getVehicleScoringInfo( i ); 
                    break;
                }
            }*/
        }

        StandingsTools.getDisplayedVSIsForScoring(scoringInfo, comparedVSI, false, StandingsView.RELATIVE_TO_LEADER, true, vehicleScoringInfos);
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = vehicleScoringInfos[i];
            
            if(vsi != null && vsi.getFinishStatus() != FinishStatus.DQ)
            {

                positions[i].update( vsi.getPlace( false ) );
                driverNames[i].update(gen.generateThreeLetterCode2( vsi.getDriverName(), gameData.getFileSystem().getConfigFolder() ));
                manufacturer[i].update( vsi.getVehicleInfo().getManufacturer().toUpperCase() );
                IsInPit[i].update( vsi.isInPits() );
                
                if(vsi.getFinishStatus() == FinishStatus.FINISHED)
                    IsFinished[i].update( true );
                else
                    IsFinished[i].update( false );
                
                gaps[i].setUnchanged();
                gaps[i].update(vsi.getBestLapTime());
                gapFlag[i] = gaps[i].hasChanged( false ) || isEditorMode;
                gapFlag2[i] = gapFlag[i];// || gapFlag2[i];
                if((IsInPit[i].hasChanged() || IsFinished[i].hasChanged()) && !isEditorMode)
                    forceCompleteRedraw( true );  
            }
        }
        
        if((scoringInfo.getSessionNanos() >= visibleEnd) && (visibleEnd != -1L))
        {
            visibleEnd = -1L;
            if ( !isEditorMode )
                forceCompleteRedraw( true );
        }
        
        if(!gaps[0].isValid())
            visibleEnd = -1L;
        else if(gapFlag[0])
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
        
        for(int i=1;i < drawncars;i++)
        {
            if(gaps[i].isValid())
            {
                if(gapFlag[i] && !isEditorMode )
                {
                    //search if the time really changed or just the position before redrawing
                    for(int j=0;j < drawncars; j++)
                    {
                        if ( vehicleScoringInfos[i].getDriverId() == driverIDs[j] )
                        {
                            if(gaps[i].getValue() == gaps[j].getOldValue())
                            {
                                gapFlag[i] = false;
                                break;
                            }
                        }
                    }
                }
                
                if((scoringInfo.getSessionNanos() >= visibleEndArray[i]) && (visibleEndArray[i] != -1L))
                {
                    visibleEndArray[i] = -1L;
                    if ( !isEditorMode )
                        forceCompleteRedraw( true );
                }
                
                if(gapFlag[i]) 
                {
                    visibleEndArray[i] = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
                    if ( !isEditorMode )
                        forceCompleteRedraw( true );
                }
            }
        }
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = vehicleScoringInfos[i];
            
            if(vsi != null)
            {
                driverIDs[i] = vsi.getDriverId();
            }
        }
        
        int nv = 0;
        for(int i=0;i < drawncars;i++)
        {
            if(gaps[i].isValid())
                nv++;
        }
        
        numValid.update( nv );
        if ( numValid.hasChanged() && !isEditorMode )
            forceCompleteRedraw( true );
        
        if( gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() > 0 || isEditorMode)
        {
            return true;
        }
        
        return false;
        
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int maxNumItems = numVeh.getValue();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        int rowHeight = height / maxNumItems;
        
        if(gaps[0].isValid())
        {
            if(scoringInfo.getSessionNanos() < visibleEnd )
                texture.clear( imgPlus.getTexture(), offsetX + width*58/100, offsetY + rowHeight/2, false, null );
            
            texture.clear( imgName.getTexture(), offsetX + width*16/100, offsetY, false, null );
           
            texture.clear( imgFirst.getTexture(), offsetX, offsetY, false, null );
          
            
            if(manufacturer[0].getValue().contains("MERCEDES") || manufacturer[0].getValue().contains("AMG") || manufacturer[0].getValue().contains("DAIMLER"))
                texture.drawImage( texMercedes, offsetX + width*17/100, offsetY, true, null );
            else if(manufacturer[0].getValue().contains("AUDI") || manufacturer[0].getValue().contains("ABT"))
                texture.drawImage( texAudi, offsetX + width*17/100, offsetY, true, null );
            else if(manufacturer[0].getValue().contains("OPEL"))
                texture.drawImage( texOpel, offsetX + width*17/100, offsetY, true, null );
            else if(manufacturer[0].getValue().contains("BMW"))
                texture.drawImage( texBMW, offsetX + width*17/100, offsetY, true, null );
             
        }
        
        
        for(int i=1;i < drawncars;i++)
        {
            if(gaps[i].isValid())
            {
                if(scoringInfo.getSessionNanos() < visibleEndArray[i])
                    texture.clear( imgPlus.getTexture(), offsetX + width*58/100, offsetY+rowHeight*i + rowHeight/2, false, null );
                
                if(positions[i].getValue() == knockout || positions[i].getValue() == knockout + 1)
                {
                    texture.clear( imgNameNew.getTexture(), offsetX + width*16/100, offsetY+rowHeight*i, false, null );
                    
                    if(positions[i].getValue() == knockout + 1)
                        texture.clear( imgPosKnockOut.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                    else
                        texture.clear( imgPos.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                    
                }
                else  
                    {
                        texture.clear( imgPos.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                        texture.clear( imgName.getTexture(), offsetX + width*16/100, offsetY+rowHeight*i, false, null );
                    }
               if(manufacturer[i].getValue().contains("MERCEDES") || manufacturer[i].getValue().contains("AMG") || manufacturer[i].getValue().contains("DAIMLER"))
                    texture.drawImage( texMercedes, offsetX + width*17/100, offsetY+rowHeight*i, true, null );
               else if(manufacturer[i].getValue().contains("AUDI") || manufacturer[i].getValue().contains("ABT"))
                   texture.drawImage( texAudi, offsetX + width*17/100, offsetY+rowHeight*i, true, null );
               else if(manufacturer[i].getValue().contains("OPEL"))
                   texture.drawImage( texOpel, offsetX + width*17/100, offsetY+rowHeight*i, true, null );
               else if(manufacturer[i].getValue().contains("BMW"))
                   texture.drawImage( texBMW, offsetX + width*17/100, offsetY+rowHeight*i, true, null );
                
                
            }
            
        }
        
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        
        //one time for leader
        
        if ( needsCompleteRedraw || ( clock.c() && gapFlag2[0]))
        {
            if(gaps[0].isValid())
            {
                dsPos[0].draw( offsetX, offsetY, "IN", texture );
                if(IsInPit[0].getValue())
                    dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(), fontColor3.getColor(), texture );
                else if(scoringInfo.getSessionNanos() < visibleEnd )
                    dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(), fontColor4.getColor(), texture );
                else
                    dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(), texture );
                
            }
            
            
            gapFlag2[0] = false;
            
        }
        
        // the other guys
        for(int i=1;i < drawncars;i++)
        { 
            if ( needsCompleteRedraw || ( clock.c() && gapFlag2[i]))
            {
                if(gaps[i].isValid())
                {
                    if(positions[i].getValue() == knockout + 1)
                        dsPos[i].draw( offsetX, offsetY, "OUT", texture );
                    if(IsInPit[i].getValue())
                        dsName[i].draw( offsetX, offsetY, driverNames[i].getValue(), fontColor3.getColor(), texture );  
                    else if(scoringInfo.getSessionNanos() < visibleEndArray[i])
                        dsName[i].draw( offsetX, offsetY, driverNames[i].getValue(), fontColor4.getColor(), texture );  
                    else
                        dsName[i].draw( offsetX, offsetY, driverNames[i].getValue() , texture );  
                    
                    if(positions[i].getValue() == knockout || positions[i].getValue() == knockout + 1)
                    {
                        if(positions[i].getValue() == knockout)
                            dsTime[i].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[i].getValue() ), texture);
                        else
                            dsTime[i].draw( offsetX, offsetY,"+" + TimingUtil.getTimeAsLaptimeString(Math.abs( gaps[i].getValue() - gaps[i-1].getValue() )) , texture);
                    }
                   
                }
                
                gapFlag2[i] = false;
                
                
            }
            
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( DTMFont, "" );
        writer.writeProperty( TeamFont, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( fontColor4, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( visibleTime, "" );
        writer.writeProperty( knockoutQual, "" );
        writer.writeProperty( knockoutFP1, "" );
        writer.writeProperty( knockoutFP2, "" );
        writer.writeProperty( knockoutFP3, "" );
        writer.writeProperty( knockoutFP4, "" );
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
        else if ( loader.loadProperty( TeamFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( fontColor4 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( visibleTime ) );
        else if ( loader.loadProperty( knockoutQual ) );
        else if ( loader.loadProperty( knockoutFP1 ) );
        else if ( loader.loadProperty( knockoutFP2 ) );
        else if ( loader.loadProperty( knockoutFP3 ) );
        else if ( loader.loadProperty( knockoutFP4 ) );
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
        propsCont.addProperty( TeamFont );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor3 );
        propsCont.addProperty( fontColor4 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
        propsCont.addProperty( numVeh );
        propsCont.addProperty( visibleTime );
        propsCont.addGroup( "Knockout Infos" );
        propsCont.addProperty( knockoutQual );
        propsCont.addProperty( knockoutFP1 );
        propsCont.addProperty( knockoutFP2 );
        propsCont.addProperty( knockoutFP3 );
        propsCont.addProperty( knockoutFP4 );
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
    
    public QualifTimingTowerWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011, 24f, 52.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME );
    }
}
