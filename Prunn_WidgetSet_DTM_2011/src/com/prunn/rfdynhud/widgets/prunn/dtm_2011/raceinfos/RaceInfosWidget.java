package com.prunn.rfdynhud.widgets.prunn.dtm_2011.raceinfos;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;

import net.ctdp.rfdynhud.gamedata.Laptime;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
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
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceInfosWidget extends Widget
{
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    private DrawnString dsPos = null;
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private DrawnString dsTime = null;
    private DrawnString dsTitle = null;
    private DrawnString dsTimeB = null;
    private DrawnString dsTitleB = null;
    private DrawnString dsTimeC = null;
    private DrawnString dsTitle2 = null;
    private DrawnString dsTitle3 = null;
    private DrawnString dsWinner = null;
    private final ImagePropertyWithTexture imgPosBig = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgNameW = new ImagePropertyWithTexture( "imgName", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgTeamF = new ImagePropertyWithTexture( "imgTeam", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgTeamW = new ImagePropertyWithTexture( "imgTeam", "prunn/DTM/black_alpha.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/f1_2011/race_gap.png" );
    private final ImagePropertyWithTexture imgTimeF = new ImagePropertyWithTexture( "imgTimeF", "prunn/f1_2011/data_faster_small.png" );
    private final ImagePropertyWithTexture imgTitle = new ImagePropertyWithTexture( "imgTitle", "prunn/f109/title.png" );
    private final ImagePropertyWithTexture imgTitleW = new ImagePropertyWithTexture( "imgTitleW", "prunn/DTM/winner.png" );
    private final ImagePropertyWithTexture imgTitleF = new ImagePropertyWithTexture( "imgTitle", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgSC = new ImagePropertyWithTexture( "imgTitle", "prunn/DTM/sc_info.png" );
    
    
    private final FontProperty posFont = new FontProperty("positionFont", PrunnWidgetSetDTM_2011.POS_FONT_NAME);
    protected final FontProperty DTMFont = new FontProperty("Main Font", PrunnWidgetSetDTM_2011.DTM_FONT_NAME);
    protected final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME);
    protected final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME);
    protected final ColorProperty fontColor5 = new ColorProperty("fontColor5", PrunnWidgetSetDTM_2011.FONT_COLOR5_NAME);
    protected final BooleanProperty showwinner = new BooleanProperty("Show Winner", "showwinner", true);
    protected final BooleanProperty showfastest = new BooleanProperty("Show Fastest Lap", "showfastest", true);
    protected final BooleanProperty showpitstop = new BooleanProperty("Show Pitstop", "showpitstop", true);
    protected final BooleanProperty showinfo = new BooleanProperty("Show Info", "showinfo", true);
    private BooleanProperty uppercasename = new BooleanProperty("uppercase name",true); 
    
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private float timestamp = -1;
    private float endtimestamp = -1;
    private float pittime = -1;
    private BoolValue isInPit = new BoolValue(false);
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private long visibleEndSC;
    private long visibleEndPitStop;
    private IntValue cveh = new IntValue();
    private IntValue speed = new IntValue();
    private long visibleEndW;
    private long visibleEndF;
    private final FloatValue racetime = new FloatValue( -1f, 0.1f );
    private float sessionstart = 0;
    private BoolValue racefinished = new BoolValue();
    private final EnumValue<YellowFlagState> SCState = new EnumValue<YellowFlagState>();
    
    private int widgetpart = 0;//0-info 1-pitstop 2-fastestlap 3-winner
    private final FloatValue FastestLapTime = new FloatValue(-1F, 0.001F);
    StandardTLCGenerator gen = new StandardTLCGenerator();
    
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
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
        int fhPos = TextureImage2D.getStringHeight( "0%C", posFont );
        int rowHeight = height / 3;
        int fw2 = Math.round(width * 0.6f);
        int fw3 = width - fw2;
        
        imgPosBig.updateSize( width*12/100, rowHeight*2, isEditorMode );
        imgName.updateSize( width*58/100, rowHeight, isEditorMode );
        imgTeam.updateSize( width*58/100, rowHeight, isEditorMode );
        imgTeamF.updateSize( width*30/100, rowHeight*2, isEditorMode );
        imgTime.updateSize( fw3, rowHeight, isEditorMode );
        imgTimeF.updateSize( fw3, rowHeight, isEditorMode );
        imgTitle.updateSize( fw3, rowHeight, isEditorMode );
        imgTitleF.updateSize( width*40/100, rowHeight, isEditorMode );
        imgTitleW.updateSize( width*40/100, rowHeight, isEditorMode );
        imgTeamW.updateSize( width*34/100, rowHeight, isEditorMode );
        imgNameW.updateSize( width*34/100, rowHeight, isEditorMode );
        imgSC.updateSize( width*58/100, rowHeight, isEditorMode );
        
        int top1 = ( rowHeight - fh ) / 2 + fontyoffset.getValue();
        int top2 = ( rowHeight - fh ) / 2 + rowHeight + fontyoffset.getValue();
        int top3 = ( rowHeight - fh ) / 2 + rowHeight*2 + fontyoffset.getValue();
        int topPos = rowHeight*2 - fhPos/2 + fontyoffset.getValue();
        
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*6/100, topPos, Alignment.CENTER, false, posFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        dsName = drawnStringFactory.newDrawnString( "dsName", width*14/100, top2, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", width*14/100, top3, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor1.getColor(), null, "" );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width*98/100, top2, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTitle = drawnStringFactory.newDrawnString( "dsTitle", width*98/100, top3, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTimeB = drawnStringFactory.newDrawnString( "dsTimeB", width*99/100, top2, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTimeC = drawnStringFactory.newDrawnString( "dsTimeC", width*68/100, top2, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor5.getColor(), null, "" );
        dsTitleB = drawnStringFactory.newDrawnString( "dsTitleB", width*68/100, top3, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTitle2 = drawnStringFactory.newDrawnString( "dsTitle2", width*98/100, top1, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTitle3 = drawnStringFactory.newDrawnString( "dsTitle2", width*99/100, top3, Alignment.RIGHT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsWinner = drawnStringFactory.newDrawnString( "dsWinner", width*60/100, top2, Alignment.LEFT, false, DTMFont.getFont(), isFontAntiAliased(), fontColor5.getColor(), null, "" );
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        cveh.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getDriverId());
        isInPit.update(scoringInfo.getViewedVehicleScoringInfo().isInPits());
        //fastest lap
        Laptime lt = scoringInfo.getFastestLaptime();
        
        if(lt == null || !lt.isFinished())
            FastestLapTime.update(-1F);
        else
            FastestLapTime.update(lt.getLapTime());
        //winner part
        if(gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted() < 1)
            sessionstart = gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapStartTime();
        if(scoringInfo.getSessionTime() > 0)
            racetime.update( scoringInfo.getSessionTime() - sessionstart );
        
        racefinished.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getFinishStatus().isFinished());
        
               
        //carinfo
        if(cveh.hasChanged() && cveh.isValid() && showinfo.getValue() && !isEditorMode)
        {
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 0;
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd )
        {
            forceCompleteRedraw(true);
            isvisible = true;
            widgetpart = 0;
            return true;
        }
        
        //pitstop   
        if( isInPit.hasChanged())
        {
            if(isInPit.getValue())
            {
                pittime = 0;
                forceCompleteRedraw(true);
            }
            
            
            endtimestamp = 0;
            timestamp = 0;
            
        }
            
        if( isInPit.getValue() && showpitstop.getValue() )
        {
            if(scoringInfo.getViewedVehicleScoringInfo().getLapsCompleted() > 0)
                widgetpart = 1;
            else
            {
                widgetpart = 0;
            }
            
            speed.update( (int)scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity());
            if(speed.hasChanged() && speed.getValue() < 20)
            {//ai gets to 5-6 kmh when they drop
                endtimestamp = gameData.getScoringInfo().getSessionTime();
                timestamp = gameData.getScoringInfo().getSessionTime();
            }
            else
                if(speed.getValue() < 2)
                    endtimestamp = gameData.getScoringInfo().getSessionTime();
                
            
            visibleEndPitStop = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEndPitStop )
        {
            forceCompleteRedraw(true);
            isvisible = true;
            widgetpart = 1;
            return true;
        }
        
        //fastest lap
        if(scoringInfo.getSessionNanos() < visibleEndF && FastestLapTime.isValid())
        {
            isvisible = true;
            widgetpart = 2;
            return true; 
        }
        if(FastestLapTime.hasChanged() && FastestLapTime.isValid() && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() > 1 && showfastest.getValue())
        {
            forceCompleteRedraw(true);
            visibleEndF = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 2;
            return true;
        }
        ///SC
        
        SCState.update(scoringInfo.getYellowFlagState());
        
        
        if(scoringInfo.getSessionNanos() < visibleEndSC)
            return true;
        
        if(SCState.hasChanged() && (SCState.getValue() == YellowFlagState.PENDING || SCState.getValue() == YellowFlagState.LAST_LAP))
        {
            widgetpart = 4;
            visibleEndSC = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            return true;
        }
        //winner part
        if(scoringInfo.getSessionNanos() < visibleEndW )
        {
            isvisible = true;
            widgetpart = 3;
            return true;
        }
         
        if(racefinished.hasChanged() && racefinished.getValue() && showwinner.getValue() )
        {
            forceCompleteRedraw(true);
            visibleEndW = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos()*2;
            isvisible = true;
            widgetpart = 3;
            return true;
        }
        isvisible = false;
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        int rowHeight = height / 3;
        if(isEditorMode)
            widgetpart = 2;
        switch(widgetpart)
        {
            case 1: //Pit Stop
                    texture.clear( imgName.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgTeam.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight + height / 3, false, null );
                    break;
        
            case 2: //Fastest Lap
                    texture.clear( imgPosBig.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    texture.clear( imgName.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgTeam.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight + height / 3, false, null );
                    texture.clear( imgTeamF.getTexture(), offsetX + imgTeam.getTexture().getWidth() + imgPosBig.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgTitleF.getTexture(), offsetX + width*64/100, offsetY, false, null );
                    break;
                    
            case 3: //Winner
                    texture.clear( imgName.getTexture(), offsetX + imgPosBig.getTexture().getWidth()-width*4/100, offsetY + rowHeight, false, null );
                    texture.clear( imgTeam.getTexture(), offsetX + imgPosBig.getTexture().getWidth()-width*4/100, offsetY + rowHeight + height / 3, false, null );
                    texture.clear( imgPosBig.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    
                    texture.clear( imgNameW.getTexture(), offsetX + width*66/100, offsetY + rowHeight, false, null );
                    texture.clear( imgTeamW.getTexture(), offsetX + width*66/100, offsetY + rowHeight*2, false, null );
                    texture.clear( imgTitleW.getTexture(), offsetX + width*60/100, offsetY, false, null );
                    break;
            
            case 4: //SC
                    texture.clear( imgName.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgSC.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight + height / 3, false, null );
                    break;
        
            default: //Info
                    texture.clear( imgPosBig.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    texture.clear( imgName.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight, false, null );
                    texture.clear( imgTeam.getTexture(), offsetX + imgPosBig.getTexture().getWidth(), offsetY + rowHeight + height / 3, false, null );
                    
                    break;
        }
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	if(isEditorMode)
            widgetpart = 2;
    	
    	if ( needsCompleteRedraw || sessionTime.hasChanged() || FastestLapTime.hasChanged())
        {
    	    String pos = "";
    	    String top1info2="";
    	    String top2info1;
    	    String top2info2b="";
    	    String top3info1;
    	    String top3info2b="";
    	    String top3info2c="";
            
    	    switch(widgetpart)
            {
                case 1: //Pit Stop
                        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
                            
                        if(scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity() < 2)
                            pittime = endtimestamp - timestamp;
                        
                        //top1info1 = currentcarinfos.getDriverNameShort().toUpperCase();
                        top2info1 = "Pit Lane";
                        top3info2c = TimingUtil.getTimeAsString(pittime, false, false, true, false );
                        
                        
                        dsName.draw( offsetX, offsetY, gen.ShortNameWTCC( currentcarinfos.getDriverNameShort().toUpperCase() ), texture );
                        dsTeam.draw( offsetX, offsetY, gen.generateShortTeamNames( currentcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ), texture );
                        if(pittime > 0)
                            dsTimeC.draw( offsetX, offsetY, top3info2c, texture);
                        
                        break;
                    
                case 2: //Fastest Lap
                        VehicleScoringInfo fastcarinfos = gameData.getScoringInfo().getFastestLapVSI();
                        
                        dsPos.draw( offsetX, offsetY, String.valueOf(fastcarinfos.getPlace( false )), texture );
                        dsName.draw( offsetX, offsetY, gen.ShortNameWTCC( fastcarinfos.getDriverNameShort().toUpperCase() ), texture );
                        dsTeam.draw( offsetX, offsetY, gen.generateShortTeamNames( fastcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ), texture );
                        dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(FastestLapTime.getValue()), texture);
                        dsTitle2.draw( offsetX, offsetY, "FASTEST LAP", texture);
                        dsTitle.draw( offsetX, offsetY, "LAP " + fastcarinfos.getFastestLaptime().getLap(), texture );
                        
                        break;
                        
                case 3: //Winner
                        VehicleScoringInfo winnercarinfos = gameData.getScoringInfo().getLeadersVehicleScoringInfo();
                        
                        top3info1 = gen.generateShortTeamNames( winnercarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() );
                        
                        float laps=0;
                        
                        for(int i=1;i <= winnercarinfos.getLapsCompleted(); i++)
                        {
                            if(winnercarinfos.getLaptime(i) != null)
                                laps = winnercarinfos.getLaptime(i).getLapTime() + laps;
                            else
                            {
                                laps = racetime.getValue();
                                i = winnercarinfos.getLapsCompleted()+1;
                            }
                        } 
                        top1info2= TimingUtil.getTimeAsLaptimeString( laps );
                        
                        top2info1 = winnercarinfos.getDriverNameShort().toUpperCase();
                        
                        top2info2b = NumberUtil.formatFloat( gameData.getTrackInfo().getTrack().getTrackLength() * gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted() / 1000f, 1, true ) + " km";
                        top3info2b = NumberUtil.formatFloat( gameData.getTrackInfo().getTrack().getTrackLength() * gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted() / 1000f / laps * 3600, 0, false ) + " km/h";
                        
                        dsPos.draw( offsetX, offsetY, String.valueOf(winnercarinfos.getPlace( false )), texture );
                        dsName.draw( offsetX, offsetY, gen.ShortNameWTCC( top2info1 ), texture );
                        dsTeam.draw( offsetX, offsetY, top3info1, texture );
                        dsWinner.draw( offsetX, offsetY, "WINNER", texture );
                        dsTimeB.draw( offsetX, offsetY, top1info2, texture);
                        
                        dsTitleB.draw( offsetX, offsetY, top3info2b, texture );
                        dsTitle3.draw( offsetX, offsetY, top2info2b, texture );
                        
                        break;
                case 4: //SC
                    if ( needsCompleteRedraw || SCState.getValue() == YellowFlagState.PENDING)
                        dsTimeC.draw( offsetX, offsetY, "SAFETY CAR  ", fontColor2.getColor(), texture);
                    else
                        if ( needsCompleteRedraw || SCState.getValue() == YellowFlagState.LAST_LAP)
                            dsTimeC.draw( offsetX, offsetY, "SAFETY CAR IN", fontColor2.getColor(), texture );
                    break;
                        
                default: //Info
                        VehicleScoringInfo currentcarinfosInfo = gameData.getScoringInfo().getViewedVehicleScoringInfo();
                        top3info1 = gen.generateShortTeamNames( currentcarinfosInfo.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() );
                        top2info1 = currentcarinfosInfo.getDriverNameShort().toUpperCase();
                        pos = Integer.toString( currentcarinfosInfo.getPlace(false) );
                        
                        dsPos.draw( offsetX, offsetY, pos, texture );
                        dsName.draw( offsetX, offsetY, gen.ShortNameWTCC( top2info1 ), texture );
                        dsTeam.draw( offsetX, offsetY, top3info1, texture );
                        
                        break;
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
        writer.writeProperty( fontColor5, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty(showwinner, "");
        writer.writeProperty(showfastest, "");
        writer.writeProperty(showpitstop, "");
        writer.writeProperty(showinfo, "");
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( uppercasename, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( DTMFont ) );
        else if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor5 ) );
        else if(loader.loadProperty(visibleTime));
        else if ( loader.loadProperty( showwinner ) );
        else if ( loader.loadProperty( showfastest ) );
        else if ( loader.loadProperty( showpitstop ) );
        else if ( loader.loadProperty( showinfo ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( uppercasename ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( DTMFont );
        propsCont.addProperty( posFont );
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor5 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty(showwinner);
        propsCont.addProperty(showfastest);
        propsCont.addProperty(showpitstop);
        propsCont.addProperty(showinfo);
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
    
    public RaceInfosWidget()
    {
        super(PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011_Race, 55.0f, 13f);
        
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
    }
    
}
