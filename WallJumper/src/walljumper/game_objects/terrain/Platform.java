package walljumper.game_objects.terrain;

import walljumper.game_objects.AbstractGameObject;
import walljumper.tools.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Platform extends AbstractGameObject{
	private static final int numBodyImages = 6;
	private static final double numEndBodyImages = 5;
	private static final double numEndBottomBodyImages = 5;
	private TextureRegion topCornerImage, endImage, midImage, bodyImage, bottomCornerImage;
	private Array<TextureRegion> bodyImages, endBodyImages, endBottomBodyImages;
	private String bodyFileName, midFileName,topCornerFileName, endBodyFileName, endBottomBodyFileName, bottomCornerFileName;
	private int lengthX, lengthY;
	private Vector2 endDimension, midDimension;
	
	public Platform(){
		
	}
	
	public Platform(float x, float y, int width, int height){
		image = Assets.instance.platform.platMap.getValueAt((int)(Math.random() * 4));
		init(null, x, y, width, height);
	}
	
	public Platform(String platType, float x, float y, int width, int height){
		bodyImages = new Array<TextureRegion>();
		endBodyImages = new Array<TextureRegion>();
		endBottomBodyImages = new Array<TextureRegion>();
		
		topCornerFileName = platType.concat("_topcorner");
		midFileName = platType.concat("_mid");
		bodyFileName = platType.concat("_body");
		endBodyFileName = platType.concat("_endbody");
		bottomCornerFileName = platType.concat("_bottomcorner");
		endBottomBodyFileName = platType.concat("_endbottombody");
		
		
		
		

		init(platType, x, y, width, height);
		
	}
	
	private void init(String platType, float x, float y, int width, int height){
		String endFileName;
		topCornerImage = Assets.instance.platform.platMap.get(topCornerFileName);
		bottomCornerImage = Assets.instance.platform.platMap.get(bottomCornerFileName);
		midImage = Assets.instance.platform.platMap.get(midFileName);
		
		
		endDimension = new Vector2(topCornerImage.getRegionWidth(), topCornerImage.getRegionHeight());
		midDimension = new Vector2(midImage.getRegionWidth(), midImage.getRegionHeight());
		
		lengthX = (int) (Math.ceil((width - 2 * midDimension.x) / midDimension.x));
		
		lengthX = (lengthX > 0) ? lengthX : 0;
		lengthY = (int) (height / midDimension.y);
		
		
		//Pick between end images of the platform
		if(lengthY > 1){
			endFileName = platType.concat("_topcorner");

		}else{
			endFileName = platType.concat("_end");
			endImage = Assets.instance.platform.platMap.get(endFileName);

		}
		
				
		//Body's
		int index = 0;
		String specificBody;
		for(int j = 0; j < lengthY - 2; j++){
			for(int i = 0; i < lengthX; i++){
				specificBody = bodyFileName.concat("" + (int)(Math.random() * numBodyImages + 1));
				bodyImages.add(Assets.instance.platform.platMap.get(specificBody)); 
				index++;
			}
		}
		//EndBody's
		String specificEndBody;
		for(int k = 0; k < (lengthY - 2) * 2; k++){
			specificEndBody = endBodyFileName.concat("" + (int)(Math.random() * numEndBodyImages + 1));
			endBodyImages.add(Assets.instance.platform.platMap.get(specificEndBody));
		}
		
		//EndBottomBody's
		String specificEndBottomBody;
		for(int l = 0; l < lengthX; l++){
			specificEndBottomBody = endBodyFileName.concat("" + (int)(Math.random() * numEndBottomBodyImages + 1));
			endBottomBodyImages.add(Assets.instance.platform.platMap.get(specificEndBottomBody));
		}
		
		//set basic vectors of position, dimension and bounds for collision
		position.set(x, y - height);
		dimension.set(width, height);
		bounds.set(position.x, position.y, endDimension.x * 2 + midDimension.x * lengthX,
				midDimension.y * lengthY - 5);
		
	}
	
	/*public void setLength(int lengthX, int lengthY){
		bounds.setSize(lengthX * dimension.x, lengthY * dimension.y - 3);
	}*/
	
	
	@Override
	public void render(SpriteBatch batch) {
		if(lengthY < 2){
			renderSingleRowPlat(batch);
			return;
		}
		float relX = 0;
		//TopCorner Left
		batch.draw(topCornerImage.getTexture(), position.x, position.y + lengthY * midDimension.y - midDimension.y, endDimension.x, 
				endDimension.y, topCornerImage.getRegionX(), topCornerImage.getRegionY(),
				topCornerImage.getRegionWidth(), topCornerImage.getRegionHeight(), false, false);
		relX += endDimension.x;
		//Top Row
		for(int i = 0; i < lengthX; i++){
			batch.draw(midImage.getTexture(), position.x + i * midDimension.x + endDimension.x,
					position.y + lengthY * midDimension.y - midDimension.y, midDimension.x, 
					midDimension.y,  midImage.getRegionX(), midImage.getRegionY(),
					midImage.getRegionWidth(), midImage.getRegionHeight(), false, false);
			relX += midDimension.x;
		}
		
		//TopCorner Right
		batch.draw(topCornerImage.getTexture(), position.x + relX, position.y + lengthY * midDimension.y - midDimension.y, endDimension.x, 
				endDimension.y, topCornerImage.getRegionX(), topCornerImage.getRegionY(),
				topCornerImage.getRegionWidth(), topCornerImage.getRegionHeight(), true, false);
		
		//Left and right Column
		TextureRegion endBodyImage;
		for(int p = 0; p < lengthY - 2; p++){
			endBodyImage = endBodyImages.get(p);
			batch.draw(endBodyImage.getTexture(), position.x, position.y + p * midDimension.y + midDimension.y, endDimension.x, 
					endDimension.y, endBodyImage.getRegionX(), endBodyImage.getRegionY(),
					endBodyImage.getRegionWidth(), endBodyImage.getRegionHeight(), false, false);
			
			endBodyImage = endBodyImages.get(p + endBodyImages.size/2 - 1);
			batch.draw(endBodyImage.getTexture(), position.x + relX, position.y + p * midDimension.y + midDimension.y, endDimension.x, 
					endDimension.y, endBodyImage.getRegionX(), endBodyImage.getRegionY(),
					endBodyImage.getRegionWidth(), endBodyImage.getRegionHeight(), true, false);
		}
		
		//Bottom Left Corner
		batch.draw(bottomCornerImage.getTexture(), position.x, position.y, endDimension.x, 
				endDimension.y, bottomCornerImage.getRegionX(), bottomCornerImage.getRegionY(),
				bottomCornerImage.getRegionWidth(), bottomCornerImage.getRegionHeight(), false, false);
		
		//Bottom Row
		TextureRegion endBottomBodyImage;
		for(int q = 0; q < endBottomBodyImages.size; q++){
			
			endBottomBodyImage = endBottomBodyImages.get(q);
			
			batch.draw(endBottomBodyImage, position.x + q * midDimension.x + endDimension.x * 2,
					position.y, 0, 0, midDimension.y, 
					midDimension.x, 1, 1, 90);
		}
		//Bottom Right Corner
				batch.draw(bottomCornerImage.getTexture(), position.x + relX, position.y, endDimension.x, 
						endDimension.y, bottomCornerImage.getRegionX(), bottomCornerImage.getRegionY(),
						bottomCornerImage.getRegionWidth(), bottomCornerImage.getRegionHeight(), true, false);
		
		
		//Body
		int index = 0;
		for(int j = 0; j < lengthY - 2; j++){
			for(int i = 0; i < lengthX; i ++){
				
				bodyImage = bodyImages.get(index);
				batch.draw(bodyImage.getTexture(), position.x + i * midDimension.x + midDimension.x, position.y + midDimension.y * j + midDimension.y, endDimension.x, 
						endDimension.y, bodyImage.getRegionX(), bodyImage.getRegionY(),
						bodyImage.getRegionWidth(), bodyImage.getRegionHeight(), false, false);
				index++;
			}
		}
		
		
	}

	private void renderSingleRowPlat(SpriteBatch batch) {
		float relX = 0;
		//TopCorner Left
		batch.draw(endImage.getTexture(), position.x, position.y + lengthY * midDimension.y - midDimension.y, endDimension.x, 
				endDimension.y, endImage.getRegionX(), endImage.getRegionY(),
				endImage.getRegionWidth(), endImage.getRegionHeight(), false, false);
		relX += endDimension.x;
		//Top Row
		for(int i = 0; i < lengthX; i++){
			batch.draw(midImage.getTexture(), position.x + i * midDimension.x + endDimension.x,
					position.y + lengthY * midDimension.y - midDimension.y, midDimension.x, 
					midDimension.y,  midImage.getRegionX(), midImage.getRegionY(),
					midImage.getRegionWidth(), midImage.getRegionHeight(), false, false);
			relX += midDimension.x;
		}
		
		//TopCorner Right
		batch.draw(endImage.getTexture(), position.x + relX, position.y + lengthY * midDimension.y - midDimension.y, endDimension.x, 
				endDimension.y, endImage.getRegionX(), endImage.getRegionY(),
				endImage.getRegionWidth(), endImage.getRegionHeight(), true, false);
		
	}

}