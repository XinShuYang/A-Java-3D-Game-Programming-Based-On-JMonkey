/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;

/**
 *
 * @author lenovo
 */
public class Music {
    AudioNode gold;
    AudioNode hit;
    AudioNode gameBg;
    AudioNode fail;
    AudioNode vectory;

   void Init(AssetManager assetManager)
   {
       gold = new AudioNode(assetManager,"Sounds/gold1.wav",false);
       hit = new AudioNode(assetManager,"Sounds/hit1.wav",false);
       gameBg = new AudioNode(assetManager,"Sounds/game.wav",false);
       gameBg.setLooping(true);
       fail = new AudioNode(assetManager,"Sounds/fail.wav",false);
       vectory = new AudioNode(assetManager,"Sounds/victory.wav",false);
   }
}
