/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author lenovo
 */
public class MapIO 
{
    ArrayList<Integer> map;
    int Score;
    int PlayerPostion;
    
    MapIO(int Score,int PlayerPostion,ArrayList<Integer> map)
    {
        this.Score = Score;
        this.PlayerPostion = PlayerPostion;
        this.map = map;
    }
    
    void save()
    {
        File f = new File(System.getProperty("user.dir") + "\\save\\save.txt");
        if (!f.exists()) 
        {
            try {
                    f.createNewFile();
            } catch (IOException e) {}
        }
        try 
        {
            FileWriter out = new FileWriter(f);
            out.write(Score+" ");
            out.write(PlayerPostion+" ");
            for(int i=0;i<map.size();i++)
                out.write(map.get(i)+" ");
             out.close();
        }
        catch (IOException e) {}
    }
    
    boolean read()
    {
         File f = new File(System.getProperty("user.dir") + "\\save\\save.txt");
        if (f.exists()) 
        {
            try {
               Scanner in = new Scanner(f);
               Score = in.nextInt();
               PlayerPostion = in.nextInt();
               map.clear();
               while(in.hasNext())
               {
                   map.add(in.nextInt());
               }
               in.close();
            } catch (Exception e) { }
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
}
