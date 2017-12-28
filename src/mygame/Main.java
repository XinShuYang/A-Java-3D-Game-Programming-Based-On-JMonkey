package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.noise.Color;
import com.jme3.ui.Picture;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    int w = 3;
    int h = 30;
    int ground_weight = 2;
    double Frame = 0;
    ArrayList<Integer> map = new ArrayList<Integer>();					//地图：0通路 1障碍 2金币
    Node mapObject = new Node();
    Node road = new Node();
    Node gold = new Node();
    Node obstruction1 = new Node();
    Node obstruction2 = new Node();
    Node player = new Node();
    AnimChannel playerChannel;
    int PlayerPostion;
    boolean End = false;
    boolean Pause = false;
    boolean ReStart = false;
    int Score = 1;
    int InsertAnimation=10;											//插入补间动画帧数
    int curAnimeFrame=0;											//当前补间帧数
    int loopNum = 1;                                                                                            //循环标示
    int state = 0;                                     //游戏状态：0开始菜单1游戏界面2暂停3游戏结束    
    int Level = 1;
    Vector3f camLocation = new Vector3f(63.364864f, 7.7545724f, 2.5679908f);
    Music mp = new Music();
    Spatial SkyBox;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        mp.Init(assetManager);
        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(10);
        //Display.
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setLocalTranslation(0, -1, 0);
        geom.setMaterial(mat);
        Spatial ground = assetManager.loadModel("Models/MOON/moon..mesh.xml");
        ground.scale(2);
        road.attachChild(ground);
        
        
        /*Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Yellow);
        Geometry geom1 = geom.clone();
        geom1.setMaterial(mat1);
        geom1.setLocalTranslation(0, 1, 0);
        gold.attachChild(geom.clone());*/
        Spatial jinbi = assetManager.loadModel("Models/jinbi3/jinbi3.mesh.xml");
        jinbi.scale(0.1f, 0.1f, 0.1f);
        jinbi.setLocalTranslation(0.0f, 2.0f, 0.0f);
        jinbi.rotate(0, 3.14f/2, 0);
        gold.attachChild(jinbi);
        gold.attachChild(ground.clone());
        
        /*Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        Geometry geom2 = geom.clone();
        geom2.setLocalTranslation(0, 1, 0);
        geom2.setMaterial(mat2);
        obstruction.attachChild(geom.clone());*/
        Spatial rock1 = assetManager.loadModel("Models/rock1/rock1.mesh.xml");
        rock1.scale(0.2f, 0.4f, 0.2f);
        rock1.setLocalTranslation(0.0f, 1.0f, 0.0f);
        obstruction1.attachChild(rock1);
        obstruction1.attachChild(ground.clone());
        Spatial rock2 = assetManager.loadModel("Models/rock2/rock2.mesh.xml");
        rock2.scale(0.2f, 0.1f, 0.1f);
        rock2.setLocalTranslation(0.0f, 1.0f, 0.0f);
        obstruction2.attachChild(rock2);
        obstruction2.attachChild(ground.clone());
        
        SkyBox = assetManager.loadModel("Models/skyn/sky.mesh.xml");
        //SkyBox.scale(0.1f);
        SkyBox.rotate(0, 3.14f/2, 0);
        rootNode.attachChild(SkyBox);
        
        player = (Node) assetManager.loadModel("Models/action/allaction.mesh.xml");
        player.setLocalScale(0.02f);
        player.rotate(0, -3.14f/2, 0);
        AnimControl control = player.getControl(AnimControl.class);
        //control.addListener(this);
        playerChannel = control.createChannel();
        playerChannel.setAnim("stand", 8f);
        playerChannel.setLoopMode(LoopMode.Loop);
        
        rootNode.attachChild(player);
        rootNode.attachChild(mapObject);
        initKey();
        guiNode.detachAllChildren();
        cam.setLocation(new Vector3f(63.364864f+2, 7.7545724f+5, 2.5679908f));
        cam.lookAtDirection(new Vector3f(-0.8977476f, -0.44020054f, -0.016516805f), new Vector3f(0f,1f,0f));
       
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(cam.getDirection().normalize());
        rootNode.addLight(sun);
        DirectionalLight sunback = new DirectionalLight();
        Vector3f v = cam.getDirection().normalize();
        Vector3f u = new Vector3f(-v.getX(),-v.getY(),-v.getZ());
        sunback.setDirection(u);
        rootNode.addLight(sunback);
        
        Restart();
        playerChannel.setAnim("stand", 8f);
        
        MapIO io = new MapIO(Score,PlayerPostion,map);
        if(io.read())
        {
            Score = io.Score;
            PlayerPostion = io.PlayerPostion;
            player.setLocalTranslation((h-3)*ground_weight, 0, PlayerPostion*ground_weight);
            MapObjectInit();
        }
        
        state = 0;
    }
    
     private ActionListener actionListener = new ActionListener()
   {
        public void onAction(String name,boolean keyPress, float tpf)
        {
            if(name.equals("Left") && !keyPress && state == 1 && End == false)
                if(PlayerPostion>0 )
                {
                        PlayerPostion --;
                        player.move(0, 0, -ground_weight);
                        keyPress = true;
                }
            if(name.equals("Right") && !keyPress && state == 1 && End == false)
                if(PlayerPostion+1<w)
                {
                        PlayerPostion ++;
                        player.move(0, 0, ground_weight);
                        keyPress = true;
                }
             if(name.equals("Restart") && !keyPress)
                if(End)
                {
                      Restart();
                }
             if(name.equals("Pause") && !keyPress)
             {
                 if(state == 1)
                 {
                      state = 2;
                      Pause();
                      playerChannel.setAnim("stand", 8f);
                 }
                 else if(state == 2)
                 {
                      state = 1;
                      playerChannel.setAnim("run", 20f);
                 }
             }
             if(name.equals("Enter") && !keyPress)
             {
                 if(state == 0)
                      state = 1;
                 playerChannel.setAnim("run", 20f);
             }
             if(name.equals("Save") && !keyPress)
             {
                 if(state==2)
                 {
                    MapIO io = new MapIO(Score,PlayerPostion,map);
                    io.save();
                    stop();
                 }
             }
             if(name.equals("Video") && !keyPress && state == 0)
             {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    File f=new File(System.getProperty("user.dir") + "\\assets\\Interface\\video.mp4");
                    desktop.open(f);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
             }
            judge();
        } 
    };
    
    void initKey()
    {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Enter", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Save", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("Video", new KeyTrigger(KeyInput.KEY_V));
        inputManager.addListener( actionListener, new String[]{"Left","Right","Restart","Pause","Enter","Save","Video"});
    }
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        switch(state)
        {
            case 0:
                MainMenu();
                break;
            case 1:
                Game();
                break;
            case 2:
        }
        if(tpf<1f/60f)
        {
            int sleepTime = (int)((1f/60f - tpf)*1000);
            try{  
                Thread.currentThread().sleep(sleepTime); //线程休眠
              }catch(InterruptedException e){}
        }
    }
    
    void judge()
    {
            int index = (h-4)*w+2-PlayerPostion;
            if(map.get(index) == 1)
            {
                map.set(index, 0);
                audioRenderer.playSource(mp.hit);
                    
                Vector3f location = mapObject.getChild(index).getLocalTranslation();
                mapObject.detachChildAt(index);
                Node temp = (Node)road.clone();
                temp.setLocalTranslation(location);
                mapObject.attachChildAt(temp, index);
                Score -=5;
                if(Score<0)
                {
                   End = true;
                   GameOver();
                   state = 3;
                }
                cam.setLocation(new Vector3f(63.364864f+1.5f, 7.7545724f+0.5f, 2.5679908f));
            }    
            else if(map.get(index) == 2)
            {
                    map.set(index, 0);
                    
                    Vector3f location = mapObject.getChild(index).getLocalTranslation();
                    mapObject.detachChildAt(index);
                    Node temp = (Node)road.clone();
                    temp.setLocalTranslation(location);
                    mapObject.attachChildAt(temp, index);
                    Score ++;
                    audioRenderer.playSource(mp.gold);
            }
    }
    
    void MapObjectMove()
    {
        SkyBox.rotate(0f, (float) (1f/10000f),0f);
        for(int i=0;i<mapObject.getChildren().size();i++)
        {
            mapObject.getChild(i).move(2.0f/InsertAnimation, 0, 0);
            if(map.get(i)==2)
            {
                Node g = (Node)mapObject.getChild(i);
                g.getChild(0).rotate(0f, 1f/(120f/InsertAnimation), 0f);
            }
        }
        Frame ++;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    void InsertRode()							//插入一行空路
    {
            for(int i=0;i<w;i++)
            {
                    map.remove(map.size()-1);
                    mapObject.detachChildAt(mapObject.getChildren().size()-1);
                    map.add(0,0);
                    addObjectToMap(0,0,i*ground_weight);
            }
    }
	
    void InsertObject()							//插入障碍/金币
    {
            int type = (int)(Math.random()*3);
            int position = (int)(Math.random()*3);
            for(int i=0;i<w;i++)
            {
                    map.remove(map.size()-1);
                    mapObject.detachChildAt(mapObject.getChildren().size()-1);
                    if(type==0)							//0两个障碍物
                    {
                            if(i==position)
                            {
                                    map.add(0,0);
                                    addObjectToMap(0,0,i*ground_weight);
                            }
                            else
                            {
                                    map.add(0,1);
                                   addObjectToMap(1,0,i*ground_weight);
                            }
                    }
                    else
                    {
                            if(i==position)
                            {
                                    map.add(0,type);
                                    addObjectToMap(type,0,i*ground_weight);
                            }
                            else
                            {
                                    map.add(0,0);
                                    addObjectToMap(0,0,i*ground_weight);
                            }
                    }
            }
    }
    
    void addObjectToMap(int type,float x,float z)
    {
        
        Node temp = null;
        switch(type)
        {
            case 0:
                temp = (Node)road.clone();
                break;
            case 1:
                if(Math.random()>0.5)
                {
                    temp = (Node)obstruction1.clone();
                    temp.getChild(0).rotate((float)Math.random(), (float)Math.random(), (float)Math.random());
                }
                else
                {
                    temp = (Node)obstruction2.clone();
                    temp.getChild(0).rotate((float)Math.random(), (float)Math.random(), (float)Math.random());
                }
                break;
            case 2:
                temp = (Node)gold.clone();
                break;
        }
        temp.setLocalTranslation(x, 0, z);
        mapObject.attachChildAt(temp,0);
    }
    
    void MapObjectInit()       
    {
        mapObject.detachAllChildren();
        for(int i=h-1;i>=0;i--)
            for(int j=w-1;j>=0;j--)
                addObjectToMap(map.get(i*w+j),i*ground_weight,(2-j)*ground_weight);        
    }
    
    void Restart()
    {
        audioRenderer.playSource(mp.gameBg);
        rootNode.detachChild(mapObject);
        map = new ArrayList<Integer>();
        mapObject = new Node();
        rootNode.attachChild(mapObject);
        PlayerPostion = 0;
        player.setLocalTranslation((h-3)*ground_weight, 0, 0);
        Score = 0;
        for(int i=0;i<h;i++)
        {
            for(int j=0;j<w;j++)
            {
                map.add(0);
                Node temp = (Node)road.clone();
                temp.setLocalTranslation(i*ground_weight, 0, j*ground_weight);
                mapObject.attachChild(temp);
            }
        }
        End = false;
        state = 1;
        Level = 1;
        InsertAnimation = 10;
        playerChannel.setAnim("run", 20f);
        playerChannel.setLoopMode(LoopMode.Loop);
    }
    
     void MainMenu()
    {
        guiNode.detachAllChildren();
        Picture p = new Picture("Picture");
        p.move(0, 0, -1); // make it appear behind stats view
        p.setPosition(0, 0);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setImage(assetManager, "Interface/Logo/main.png", false);
       guiNode.attachChild(p);
    }

    
    BitmapText Lable(String text,float x,float y)
    {
        BitmapText Text = new BitmapText(guiFont, false);
        Text.setSize(30);
        Text.setText(text);
        Text.setLocalTranslation(x,y, 0);
        return Text;
    }
    
    void Game()
    {
        guiNode.detachAllChildren();
        BitmapText ScoreText = new BitmapText(guiFont);
        BitmapText LevelText = new BitmapText(guiFont);
        guiNode.attachChild(ScoreText);
        guiNode.attachChild(LevelText);
        ScoreText.setSize(20);
        LevelText.setSize(20);
        ScoreText.setLocalTranslation(20, 25, 0);
        LevelText.setLocalTranslation(0, 60, 0);
        ScoreText.setText(""+Score);
        LevelText.setText("Level:"+Level);
        
        if(End == false)
        {
            int ObjectDistance = 6;											//物体与物体间距
            MapObjectMove();
            if(curAnimeFrame+1 == InsertAnimation)
            {
                    System.out.println("D:"+cam.getDirection());
                    System.out.println("L:"+cam.getLocation());
                    curAnimeFrame = 0;

                    if(loopNum++%ObjectDistance == 0)
                    {
                            loopNum = 1;
                            InsertObject();
                    }
                    else
                            InsertRode();
                    judge();
            }
            else
            {
                    curAnimeFrame++;
            }
            
            Picture p = new Picture("Picture");
            p.move(0, 0, -1); // make it appear behind stats view
            p.setPosition(0, 0);
            p.setWidth(20);
            p.setHeight(20);
            p.setImage(assetManager, "Interface/Logo/coin.png", false);
            
            guiNode.attachChild(p);
            if(Score>=10 && InsertAnimation==10)
            {
                InsertAnimation=7;
                Level = 2;
                audioRenderer.playSource(mp.vectory);
            }
             if(Score>=20 && InsertAnimation==7)
             {
                InsertAnimation=5;
                Level = 3;
                audioRenderer.playSource(mp.vectory);
             }
             CamTrack();
        }
    }
    
    void CamTrack()
    {
        if(!(cam.getLocation().length()-camLocation.length()<0.1f))
        {
            Vector3f v = camLocation.subtract(cam.getLocation());
            cam.setLocation(cam.getLocation().add(v.normalize().mult(0.1f)));
            System.out.println(v);
         }
    }
    
    void GameOver()
    {
        playerChannel.setAnim("down", 8f);
        playerChannel.setLoopMode(LoopMode.DontLoop);
        audioRenderer.stopSource(mp.gameBg);
        audioRenderer.playSource(mp.fail);
        Picture p = new Picture("Picture");
        p.move(0, 0, 1); // make it appear behind stats view
        p.setPosition(220, 150);
        p.setWidth(293);
        p.setHeight(158);
        p.setImage(assetManager, "Interface/Logo/gameover.png", false);
        guiNode.attachChild(p);

    }
    
    void Pause()
    {
        Picture p = new Picture("Picture");
        p.move(0, 0, 1); // make it appear behind stats view
        p.setPosition(220, 150);
        p.setWidth(293);
        p.setHeight(158);
        p.setImage(assetManager, "Interface/Logo/pause.png", false);
        guiNode.attachChild(p);

    }
}
