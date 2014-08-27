package com.prunn.rfdynhud.widgets.prunn.dtm_2011.racetower;

import java.io.IOException;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
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
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetDTM_2011;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceTowerWidget extends Widget
{
    private TextureImage2D texMercedes = null;
    private TextureImage2D texAudi = null;
    private TextureImage2D texOpel = null;
    private TextureImage2D texBMW = null;
    private TextureImage2D texPit = null;
    private final ImagePropertyWithTexture imgMercedes = new ImagePropertyWithTexture( "imgMercedes", "prunn/DTM/mercedes.png" );
    private final ImagePropertyWithTexture imgAudi = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/audi.png" );
    private final ImagePropertyWithTexture imgOpel = new ImagePropertyWithTexture( "imgMercedes", "prunn/DTM/opel.png" );
    private final ImagePropertyWithTexture imgBMW = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/bmw.png" );
    private final ImagePropertyWithTexture imgPit = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/pitstops.png" );
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/white.png" );
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgPos", "prunn/DTM/black.png" );
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetDTM_2011.FONT_COLOR2_NAME );
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetDTM_2011.FONT_COLOR3_NAME );
    private final ColorProperty fontColor4 = new ColorProperty( "fontColor4", PrunnWidgetSetDTM_2011.FONT_COLOR4_NAME );
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 14 );
    private final IntValue drawnCars = new IntValue();
    private final IntValue carsOnLeadLap = new IntValue();
    private short[] positions = null;
    private short[] gainedPlaces = null;
    private String[] names = null;
    private StringValue[] manufacturer = null;
    private IntValue[] PitStops = null;
    private boolean init = false;
    
   
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        drawnCars.reset();
        
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
        int maxNumItems = numVeh.getValue();
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
                
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems;
        
        imgPos.updateSize( width*24/100, rowHeight*95/100, isEditorMode );
        imgName.updateSize( width*63/100, rowHeight*95/100, isEditorMode );
        texMercedes = imgMercedes.getImage().getScaledTextureImage( width*24/100, rowHeight*95/100, texMercedes, isEditorMode );
        texAudi = imgAudi.getImage().getScaledTextureImage( width*24/100, rowHeight*95/100, texAudi, isEditorMode );
        texOpel = imgOpel.getImage().getScaledTextureImage( width*24/100, rowHeight*95/100, texOpel, isEditorMode );
        texBMW = imgBMW.getImage().getScaledTextureImage( width*24/100, rowHeight*95/100, texBMW, isEditorMode );
        texPit = imgPit.getImage().getScaledTextureImage( rowHeight*95/100, rowHeight*95/100, texPit, isEditorMode );
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        { 
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*12/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*52/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width - rowHeight*95/200 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor1.getColor() );
            
            top += rowHeight;
        }
        
        
    }
    private void clearArrayValues(int maxNumCars)
    {
        positions = new short[maxNumCars];
        gainedPlaces = new short[maxNumCars];
        PitStops = new IntValue[maxNumCars];
        names = new String[maxNumCars];
        manufacturer = new StringValue[maxNumCars];
        
        for(int i=0;i<maxNumCars;i++)
        {
            positions[i] = -1;
            gainedPlaces[i] = 0;
            PitStops[i] = new IntValue(0);;
            names[i] = "";
            manufacturer[i] = new StringValue("");
        }
        init = true;
    }
    private void FillArrayValues(ScoringInfo scoringInfo, boolean isEditorMode, LiveGameData gameData)
    {
        int onLeaderLap = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        for(int i=0;i<onLeaderLap;i++)
        {
            VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
            positions[i] = vsi.getPlace( false );
            names[i] = PrunnWidgetSetDTM_2011.generateThreeLetterCode2( vsi.getDriverName(), gameData.getFileSystem().getConfigFolder() );
            PitStops[i].update( vsi.getNumPitstopsMade() );
            manufacturer[i].update( vsi.getVehicleInfo().getManufacturer().toUpperCase() );
        }
    }
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        if(!init)
            clearArrayValues(scoringInfo.getNumVehicles());
        
        
        //how many on the same lap?
        int onlap = 0;
        for(int j=0;j < scoringInfo.getNumVehicles(); j++)
        {
            if(scoringInfo.getVehicleScoringInfo( j ).getLapsCompleted() == scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() )
                onlap++;
            if((manufacturer[j].hasChanged() || PitStops[j].hasChanged() ) && !isEditorMode)
                forceCompleteRedraw( true );
        }
            
        carsOnLeadLap.update( onlap );
        FillArrayValues( scoringInfo, isEditorMode, gameData);
        
        return true;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int maxNumItems = numVeh.getValue();
        int rowHeight = height / maxNumItems;
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        
        for(int i=0;i < drawncars;i++)
        {
            if(positions[i] != -1 || isEditorMode)
            {
                texture.clear( imgPos.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                texture.clear( imgName.getTexture(), offsetX + width*24/100, offsetY+rowHeight*i, false, null );
                      
                if(manufacturer[i].getValue().contains("MERCEDES") || manufacturer[i].getValue().contains("AMG") || manufacturer[i].getValue().contains("DAIMLER"))
                    texture.drawImage( texMercedes, offsetX + width*26/100, offsetY+rowHeight*i, true, null );
                else if(manufacturer[i].getValue().contains("AUDI") || manufacturer[i].getValue().contains("ABT"))
                    texture.drawImage( texAudi, offsetX + width*26/100, offsetY+rowHeight*i, true, null );
                else if(manufacturer[i].getValue().contains("OPEL"))
                    texture.drawImage( texOpel, offsetX + width*26/100, offsetY+rowHeight*i, true, null );
                else if(manufacturer[i].getValue().contains("BMW"))
                    texture.drawImage( texBMW, offsetX + width*26/100, offsetY+rowHeight*i, true, null );
               
                if(scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() >= 1)
                    texture.drawImage( texPit, offsetX + width - rowHeight*95/100, offsetY+rowHeight*i, true, null );
                
            }
        }
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        
            int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
            
            for(int i=0;i < drawncars;i++)
            { 
                if(positions[i] != -1)
                    dsPos[i].draw( offsetX, offsetY, String.valueOf(positions[i]), texture );
                                
                if(scoringInfo.getVehicleScoringInfo( i ).isInPits())
                    dsName[i].draw( offsetX, offsetY, names[i], fontColor3.getColor(),texture );
                else if(i >= carsOnLeadLap.getValue())
                    dsName[i].draw( offsetX, offsetY, names[i], fontColor4.getColor(),texture );
                else    
                    dsName[i].draw( offsetX, offsetY, names[i], texture );
                
                if(PitStops[i].getValue() > 0)
                    dsTime[i].draw( offsetX, offsetY, PitStops[i].getValueAsString(), texture );
            }
        
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( fontColor4, "" );
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
        
        if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( fontColor4 ) );
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
        
    }
    
    public RaceTowerWidget()
    {
        super( PrunnWidgetSetDTM_2011.INSTANCE, PrunnWidgetSetDTM_2011.WIDGET_PACKAGE_DTM_2011_Race, 15.3f, 54f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetDTM_2011.DTM_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetDTM_2011.FONT_COLOR1_NAME );
    }
}
