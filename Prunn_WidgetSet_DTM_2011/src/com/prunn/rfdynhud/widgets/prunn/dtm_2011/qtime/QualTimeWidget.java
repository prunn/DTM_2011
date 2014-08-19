package com.prunn.rfdynhud.widgets.prunn.dtm_2011.qtime;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.plugins.tlcgenerator.StandardTLCGenerator;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
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


public class QualTimeWidget extends Widget
{
    private static enum Situation
    {
        LAST_SECONDS_OF_SECTOR_1,
        SECTOR_1_FINISHED_BEGIN_SECTOR_2,
        LAST_SECONDS_OF_SECTOR_2,
        SECTOR_2_FINISHED_BEGIN_SECTOR_3,
        LAST_SECONDS_OF_SECTOR_LAP,
        LAP_FINISHED_BEGIN_NEW_LAP,
        OTHER,
        ;
    }
    
    private static final float SECTOR_DELAY = 5f;
    
    private DrawnString dsPos = null;
    private DrawnString dsPosFrom = null;
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private DrawnString dsTime = null;
    private DrawnString dsGap = null;
    private DrawnString dsSector1 = null;
    private DrawnString dsSector2 = null;
    private DrawnString dsSector3 = null;
    private DrawnString dsLeader = null;
    private DrawnString dsLeaderTime = null;
    
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgPosOut = new ImagePropertyWithTexture( "imgPosOut", "prunn/DTM/red.png" );
    private final ImagePropertyWithTexture imgPosSmall = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgPosOutSmall = new ImagePropertyWithTexture( "imgPosOut", "prunn/DTM/red.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgTeam = new ImagePropertyWithTexture( "imgTeam", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgSector = new ImagePropertyWithTexture( "imgTime", "prunn/DTM/black.png" );
    private final ImagePropertyWithTexture imgLeader = new ImagePropertyWithTexture( "imgTime", "prunn/DTM/leader.png" );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    StandardTLCGenerator gen = new StandardTLCGenerator();
    
    
    private final FontProperty posFont = new FontProperty("positionFont", PrunnWidgetSetDTM_2011.POS_FONT_NAME);
    protected final FontProperty TeamFont = new FontProperty("Team Font", PrunnWidgetSetDTM_2011.TEAM_FONT_NAME);
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME);
    private final ColorProperty fontColorGap1 = new ColorProperty("GapFontColor1", PrunnWidgetSetDTM_2011.GAP_FONT_COLOR1_NAME);
    private final ColorProperty fontColorGap2 = new ColorProperty("GapFontColor2", PrunnWidgetSetDTM_2011.GAP_FONT_COLOR2_NAME);
    
    private final IntProperty posKnockout = new IntProperty("positionForKnockOut", "posKnockout", 8);
    
    private final EnumValue<Situation> situation = new EnumValue<Situation>();
    private final IntValue leaderID = new IntValue();
    private final IntValue CurrentPos = new IntValue();
    private final IntValue ownPos = new IntValue();
    private float leadsec1 = -1f;
    private float leadsec2 = -1f;
    private float leadlap = -1f;
    private final FloatValue cursec1 = new FloatValue(-1f, 0.001f);
    private final FloatValue cursec2 = new FloatValue(-1f, 0.001f);
    private final FloatValue curlap = new FloatValue(-1f, 0.001f);
    private final FloatValue oldbesttime = new FloatValue(-1f, 0.001f);
    private final FloatValue gapOrTime = new FloatValue(-1f, 0.001f);
    private final FloatValue lastLaptime = new FloatValue(-1f, 0.001f);
    private final FloatValue fastestlap = new FloatValue(-1f, 0.001f);
    private final FloatValue knockoutlap = new FloatValue(-1f, 0.001f);
    private final BoolValue gapAndTimeInvalid = new BoolValue();
    private float oldbest = 0;
    private static Boolean isvisible = false;
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
        situation.reset();
        leaderID.reset();
        CurrentPos.reset();
        ownPos.reset();
        cursec1.reset();
        cursec2.reset();
        curlap.reset();
        oldbesttime.reset();
        gapOrTime.reset();
        lastLaptime.reset();
        gapAndTimeInvalid.reset();
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
        int rowHeight = height / 4;
        int fh = TextureImage2D.getStringHeight( "09gy", getFontProperty() );
        int posfh = TextureImage2D.getStringHeight( "0", posFont );
        
        imgPos.updateSize( width*12/100, rowHeight *2, isEditorMode );
        imgPosOut.updateSize( width*12/100, rowHeight *2, isEditorMode );
        imgPosSmall.updateSize( width*8/100, rowHeight, isEditorMode );
        imgPosOutSmall.updateSize( width*8/100, rowHeight, isEditorMode );
        imgName.updateSize( width*65/100, rowHeight, isEditorMode );
        imgTeam.updateSize( width*65/100, rowHeight, isEditorMode );
        imgTime.updateSize( width*33/100, rowHeight*2, isEditorMode );
        imgSector.updateSize( width*18/100, rowHeight, isEditorMode );
        imgLeader.updateSize( width*57/100, rowHeight, isEditorMode );
        
        Color blackFontColor = fontColor1.getColor();
        Color whiteFontColor = fontColor2.getColor();
        
        int textOff = ( rowHeight - fh ) / 2;
        int top1 = ( rowHeight - fh ) / 2;
        int top2 = rowHeight + ( rowHeight - fh ) / 2;
        int top3 = rowHeight*2 + ( rowHeight - fh ) / 2;
        int top4 = rowHeight*3 + ( rowHeight - fh ) / 2;
        
        dsLeader = drawnStringFactory.newDrawnString( "dsName", width*45/100, top1 + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsLeaderTime = drawnStringFactory.newDrawnString( "dsName", width*98/100, top1 + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsName = drawnStringFactory.newDrawnString( "dsName", width*14/100, top2 + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", width*14/100, top3 + fontyoffset.getValue(), Alignment.LEFT, false, TeamFont.getFont(), isFontAntiAliased(), fontColor1.getColor() );
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width*98/100, top2 + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*6/100, height/2 - posfh/2 + fontyoffset.getValue(), Alignment.CENTER, false, posFont.getFont(), isFontAntiAliased(), blackFontColor);
        dsPosFrom = drawnStringFactory.newDrawnString( "dsPosFrom", width*8/100, top2 + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), blackFontColor );
        dsGap = drawnStringFactory.newDrawnString( "dsTime", width*98/100, rowHeight * 2 + textOff + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsSector1 = drawnStringFactory.newDrawnString( "dsTime", width*28/100, top4 + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsSector2 = drawnStringFactory.newDrawnString( "dsTime", width*51/100, top4 + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsSector3 = drawnStringFactory.newDrawnString( "dsTime", width*75/100, top4 + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        
    }
    
    private VehicleScoringInfo getLeaderCarInfos( ScoringInfo scoringInfo )
    {
        return ( scoringInfo.getLeadersVehicleScoringInfo() );
    }
    
    private void updateSectorValues( ScoringInfo scoringInfo )
    {
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = getLeaderCarInfos( scoringInfo );
        
        if(leadercarinfos.getFastestLaptime() != null && leadercarinfos.getFastestLaptime().getLapTime() >= 0)
        {
            leadsec1 = leadercarinfos.getFastestLaptime().getSector1();
            leadsec2 = leadercarinfos.getFastestLaptime().getSector1And2();
            leadlap = leadercarinfos.getFastestLaptime().getLapTime();
        }
        else
        {
            leadsec1 = 0f;
            leadsec2 = 0f;
            leadlap = 0f;
        }
        
        cursec1.update( currentcarinfos.getCurrentSector1() );
        cursec2.update( currentcarinfos.getCurrentSector2( true ) );

        if ( scoringInfo.getSessionTime() > 0f )
            curlap.update( currentcarinfos.getCurrentLaptime() );
        else
            curlap.update( scoringInfo.getSessionNanos() / 1000000000f - currentcarinfos.getLapStartTime() ); 
            

    }
    
    private boolean updateSituation( VehicleScoringInfo currentcarinfos )
    {
        final byte sector = currentcarinfos.getSector();
        
        if(sector == 1 && curlap.getValue() > leadsec1 - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_1 );
        }
        else if(sector == 2 && curlap.getValue() - cursec1.getValue() <= SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.SECTOR_1_FINISHED_BEGIN_SECTOR_2 );
        }
        else if(sector == 2  && curlap.getValue() > leadsec2 - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_2 );
        }
        else if(sector == 3 && curlap.getValue() - cursec2.getValue() <= SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.SECTOR_2_FINISHED_BEGIN_SECTOR_3 );
        }
        else if(sector == 3 && curlap.getValue() > leadlap - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_LAP );
        }
        else if(sector == 1 && curlap.getValue() <= SECTOR_DELAY && currentcarinfos.getLastLapTime() > 0)
        {
            situation.update( Situation.LAP_FINISHED_BEGIN_NEW_LAP );
        }
        else
        {
            situation.update( Situation.OTHER );
        }
        
        return ( situation.hasChanged() );
    }
    
    @Override
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        updateSectorValues( scoringInfo );
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        
        
        
        fastestlap.update(scoringInfo.getLeadersVehicleScoringInfo().getBestLapTime());
        if( gameData.getScoringInfo().getNumVehicles() >= posKnockout.getValue() )
            knockoutlap.update(scoringInfo.getVehicleScoringInfo( posKnockout.getValue()-1 ).getBestLapTime());
        
        if ( (updateSituation( currentcarinfos )  || fastestlap.hasChanged() || knockoutlap.hasChanged()) && !isEditorMode)
            forceCompleteRedraw( true );
        
        if ( currentcarinfos.isInPits() )
        {
            isvisible = false;
            return false;
        }
        
        if(currentcarinfos.getFinishStatus() == FinishStatus.FINISHED && situation.getValue() != Situation.LAP_FINISHED_BEGIN_NEW_LAP )
            return false;
        
        float curLaptime;
        if ( scoringInfo.getSessionTime() > 0f )
            curLaptime = currentcarinfos.getCurrentLaptime();
        else
            curLaptime = scoringInfo.getSessionNanos() / 1000000000f - currentcarinfos.getLapStartTime();
        
        if ( curLaptime > 0f )
        {
            isvisible = true;
            //forceCompleteRedraw( true );
            return true;
        }
            
        return false;
         
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        VehicleScoringInfo currentcarinfos = gameData.getScoringInfo().getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = getLeaderCarInfos( scoringInfo );
         
        int rowHeight = height / 4;
        
        texture.clear( imgName.getTexture(), offsetX + width*12/100, offsetY + rowHeight, false, null );
        texture.clear( imgTeam.getTexture(), offsetX + width*12/100, offsetY + rowHeight*2, false, null );
        texture.clear( imgTime.getTexture(), offsetX + width*77/100, offsetY + rowHeight, false, null );
        if(leadercarinfos.getFastestLaptime() == null)
        {
            if(currentcarinfos.getSector() >= 2)
                texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
            if(currentcarinfos.getSector() == 3)
                texture.clear( imgSector.getTexture(), offsetX + width*35/100, offsetY + rowHeight*3, false, null );
            
        }
        switch ( situation.getValue() )
        {
            case LAST_SECONDS_OF_SECTOR_1:
                if(leadercarinfos.getFastestLaptime() != null)
                    texture.clear( imgLeader.getTexture(), offsetX + width*43/100, offsetY, false, null );
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPosSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOutSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                
                break;
                
            case SECTOR_1_FINISHED_BEGIN_SECTOR_2:
                if(leadercarinfos.getFastestLaptime() != null)
                    texture.clear( imgLeader.getTexture(), offsetX + width*43/100, offsetY, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPosSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOutSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                
                break;
                
            case LAST_SECONDS_OF_SECTOR_2:
                if(leadercarinfos.getFastestLaptime() != null)
                    texture.clear( imgLeader.getTexture(), offsetX + width*43/100, offsetY, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPosSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOutSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                
                break;
                
            case SECTOR_2_FINISHED_BEGIN_SECTOR_3:
                if(leadercarinfos.getFastestLaptime() != null)
                    texture.clear( imgLeader.getTexture(), offsetX + width*43/100, offsetY, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*35/100, offsetY + rowHeight*3, false, null );
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPosSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOutSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                
                break;
                
            case LAST_SECONDS_OF_SECTOR_LAP:
                if(leadercarinfos.getFastestLaptime() != null)
                    texture.clear( imgLeader.getTexture(), offsetX + width*43/100, offsetY, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*35/100, offsetY + rowHeight*3, false, null );
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPosSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOutSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                
                break;
                
            case LAP_FINISHED_BEGIN_NEW_LAP:
                texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*35/100, offsetY + rowHeight*3, false, null );
                texture.clear( imgSector.getTexture(), offsetX + width*59/100, offsetY + rowHeight*3, false, null );
                if(isEditorMode)
                    texture.clear( imgLeader.getTexture(), offsetX + width*43/100, offsetY, false, null );
                
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPos.getTexture(), offsetX, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOut.getTexture(), offsetX, offsetY + rowHeight, false, null );
                
                break;     
            case OTHER:
                if(currentcarinfos.getSector() >= 2)
                    texture.clear( imgSector.getTexture(), offsetX + width*12/100, offsetY + rowHeight*3, false, null );
                if(currentcarinfos.getSector() == 3)
                    texture.clear( imgSector.getTexture(), offsetX + width*35/100, offsetY + rowHeight*3, false, null );
                if(currentcarinfos.getPlace( false ) <= posKnockout.getValue())    
                    texture.clear( imgPosSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                else
                    texture.clear( imgPosOutSmall.getTexture(), offsetX + width*4/100, offsetY + rowHeight, false, null );
                
                break;
                
            
        }
    }
    
    private static final String getTimeAsGapString2( float gap )
    {
        if ( gap == 0f )
            return ( "- " + TimingUtil.getTimeAsLaptimeString( 0f ) );
        
        if ( gap < 0f )
            return ( "- " + TimingUtil.getTimeAsLaptimeString( -gap ) );
        
        return ( "+ " + TimingUtil.getTimeAsLaptimeString( gap ) );
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        updateSectorValues( scoringInfo );
        
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = getLeaderCarInfos( scoringInfo );
        
        leaderID.update( leadercarinfos.getDriverId() );
        CurrentPos.update( currentcarinfos.getPlace( false ) );
        
        
        if ( needsCompleteRedraw || ( clock.c() && leaderID.hasChanged() ) )
        {
            dsName.draw( offsetX, offsetY, gen.ShortNameWTCC( currentcarinfos.getDriverNameShort().toUpperCase()), texture );
            dsTeam.draw( offsetX, offsetY, gen.generateShortTeamNames( currentcarinfos.getVehicleInfo().getTeamName(), gameData.getFileSystem().getConfigFolder() ), texture );
        }
        
        
        switch ( situation.getValue() )
        {
            case LAST_SECONDS_OF_SECTOR_1:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( leadsec1 );
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeader.draw( offsetX, offsetY, gen.ShortNameWTCC(leadercarinfos.getDriverName()) , texture);
                    dsLeaderTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(leadercarinfos.getFastestLaptime().getSector1()) , texture);
                }
                if ( needsCompleteRedraw || ( clock.c() && CurrentPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, CurrentPos.getValueAsString(), texture );  
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                
            case SECTOR_1_FINISHED_BEGIN_SECTOR_2:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( cursec1.getValue() - leadsec1 );
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeader.draw( offsetX, offsetY, gen.ShortNameWTCC(leadercarinfos.getDriverName()) , texture);
                    dsLeaderTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(leadercarinfos.getFastestLaptime().getSector1()) , texture);
                }
                dsSector1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector1()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < leadercarinfos.getFastestLaptime().getSector1()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < currentcarinfos.getFastestLaptime().getSector1())? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ) , texture);
                if ( needsCompleteRedraw || ( clock.c() && CurrentPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, CurrentPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && cursec1.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( cursec1.getValue(), false, false, true, true ) , texture);
                break;
                
            case LAST_SECONDS_OF_SECTOR_2:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( leadsec2 );
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeader.draw( offsetX, offsetY, gen.ShortNameWTCC(leadercarinfos.getDriverName()) , texture);
                    dsLeaderTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(leadercarinfos.getFastestLaptime().getSector2( true )) , texture);
                }
                dsSector1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector1()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < leadercarinfos.getFastestLaptime().getSector1()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < currentcarinfos.getFastestLaptime().getSector1())? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                
                //if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    //dsGap.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( leadsec2 ), fontColor2.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && CurrentPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, CurrentPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                
            case SECTOR_2_FINISHED_BEGIN_SECTOR_3:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( cursec2.getValue() - leadsec2 );
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeader.draw( offsetX, offsetY, gen.ShortNameWTCC(leadercarinfos.getDriverName()) , texture);
                    dsLeaderTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(leadercarinfos.getFastestLaptime().getSector2( true )) , texture);
                }
                dsSector1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector1()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < leadercarinfos.getFastestLaptime().getSector1()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < currentcarinfos.getFastestLaptime().getSector1())? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                dsSector2.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector2(false)),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector2(false) < leadercarinfos.getFastestLaptime().getSector2(false)) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector2(false) < currentcarinfos.getFastestLaptime().getSector2(false))? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ) , texture);
                if ( needsCompleteRedraw || ( clock.c() && CurrentPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, CurrentPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && cursec2.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( cursec2.getValue(), false, false, true, true ) , texture);
                break;
                
            case LAST_SECONDS_OF_SECTOR_LAP:
                gapAndTimeInvalid.update( false );
                gapOrTime.update( leadlap );
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeader.draw( offsetX, offsetY, gen.ShortNameWTCC(leadercarinfos.getDriverName()) , texture);
                    dsLeaderTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(leadercarinfos.getFastestLaptime().getLapTime()) , texture);
                }
                dsSector1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector1()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < leadercarinfos.getFastestLaptime().getSector1()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < currentcarinfos.getFastestLaptime().getSector1())? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                dsSector2.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector2(false)),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector2(false) < leadercarinfos.getFastestLaptime().getSector2(false)) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector2(false) < currentcarinfos.getFastestLaptime().getSector2(false))? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                
                //if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                //    dsGap.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString( gapOrTime.getValue() ), fontColor2.getColor() , texture);
                if ( needsCompleteRedraw || ( clock.c() && CurrentPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, CurrentPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                
            case LAP_FINISHED_BEGIN_NEW_LAP:
                //plan: if allready first show gap to previous own best time. else if newly first show gap to second                             
                
                float secondbest=0;
                oldbesttime.update( currentcarinfos.getBestLapTime() );
                //oldposition.update( currentcarinfos.getPlace( false ) );
                ownPos.update( currentcarinfos.getPlace( false ) );
                
                if(oldbesttime.hasChanged())
                    oldbest = oldbesttime.getOldValue();
                
                if(ownPos.getValue() == 1)
                {
                    if(gameData.getScoringInfo().getSecondFastestLapVSI() != null && ownPos.getValue() != 1)
                        secondbest = gameData.getScoringInfo().getSecondFastestLapVSI().getBestLapTime(); 
                    else
                        secondbest = oldbest;
                }
                else
                    if(ownPos.getValue() == posKnockout.getValue())
                    {
                        //second best p10-11
                        if(gameData.getScoringInfo().getVehicleScoringInfo( posKnockout.getValue()-1 ) != null && ownPos.getValue() != posKnockout.getValue())
                            secondbest = gameData.getScoringInfo().getVehicleScoringInfo( posKnockout.getValue()-1 ).getBestLapTime(); 
                        else
                            secondbest = oldbest;
                    }
                
            
               
                dsSector1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getLastSector1()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector1() < leadercarinfos.getFastestLaptime().getSector1()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector1() < currentcarinfos.getFastestLaptime().getSector1())? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                dsSector2.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getLastSector2(false)),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector2(false) < leadercarinfos.getFastestLaptime().getSector2(false)) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector2(false) < currentcarinfos.getFastestLaptime().getSector2(false))? fontColorGap2.getColor() : fontColor2.getColor(), texture);
                dsSector3.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getLastSector3()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector3() < leadercarinfos.getFastestLaptime().getSector3()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector3() < currentcarinfos.getFastestLaptime().getSector3())? fontColorGap2.getColor() : fontColor2.getColor(), texture);
                
                if (currentcarinfos.getLastLapTime() <= leadercarinfos.getBestLapTime() && secondbest < 0)
                    gapOrTime.update(  currentcarinfos.getLastLapTime() - oldbest );
                else
                    if( currentcarinfos.getLastLapTime() <= leadercarinfos.getBestLapTime() )
                        gapOrTime.update( currentcarinfos.getLastLapTime() - secondbest );
                    else
                        gapOrTime.update( currentcarinfos.getLastLapTime() - leadercarinfos.getBestLapTime() );
                    
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), texture);
                
                
                
                lastLaptime.update( currentcarinfos.getLastLapTime() );
                gapAndTimeInvalid.update( false );
                
                
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                    dsPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                if ( needsCompleteRedraw || ( clock.c() && lastLaptime.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( lastLaptime.getValue(), false, false, true, true ) , texture);
                if(isEditorMode)
                {
                    dsLeader.draw( offsetX, offsetY, gen.ShortNameWTCC(leadercarinfos.getDriverName()) , texture);
                    dsLeaderTime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(leadercarinfos.getFastestLaptime().getSector2( true )) , texture);
                }
                break;
              
            case OTHER:
                // other cases not info not drawn
                gapAndTimeInvalid.update( true );
                
                if(currentcarinfos.getSector() >= 2)
                    dsSector1.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector1()),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < leadercarinfos.getFastestLaptime().getSector1()) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector1() < currentcarinfos.getFastestLaptime().getSector1())? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                if(currentcarinfos.getSector() == 3)
                    dsSector2.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(currentcarinfos.getCurrentSector2(false)),(leadercarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector2(false) < leadercarinfos.getFastestLaptime().getSector2(false)) ? fontColorGap1.getColor() : (currentcarinfos.getFastestLaptime() == null || currentcarinfos.getCurrentSector2(false) < currentcarinfos.getFastestLaptime().getSector2(false))? fontColorGap2.getColor() : fontColor2.getColor() , texture);
                
                if ( needsCompleteRedraw || ( clock.c() && CurrentPos.hasChanged() ) )
                    dsPosFrom.draw( offsetX, offsetY, CurrentPos.getValueAsString(), texture );  
                
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, TimingUtil.getTimeAsString( curlap.getValue(), false, false, true, false ) + "    ", texture);
                break;
                 
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColorGap1, "" );
        writer.writeProperty( fontColorGap2, "" );
        writer.writeProperty( TeamFont, "" );
        writer.writeProperty( posFont, "" );
        writer.writeProperty( posKnockout, "" );
        writer.writeProperty( fontyoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( TeamFont ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColorGap1 ) );
        else if ( loader.loadProperty( fontColorGap2 ) );
        else if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty( posKnockout ) );
        else if ( loader.loadProperty( fontyoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        
        propsCont.addProperty( fontColor1 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColorGap1 );
        propsCont.addProperty( fontColorGap2 );
        propsCont.addProperty( TeamFont );
        propsCont.addProperty( posFont );
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( posKnockout );
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
    
    public QualTimeWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011, 49f, 17f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
    }
    
}
