package android.drewgame.game.game;

import android.drewgame.game.audio.Audio;
import android.drewgame.game.fileio.FileIO;
import android.drewgame.game.graphics.Screen;
import android.drewgame.game.input.Input;

public interface Game
{
	public Input getInput();
	
	public FileIO getFileIO();
	
	public Audio getAudio();
	
	public void setScreen(Screen screen);
	
	public Screen getCurrentScreen();
	
	public Screen getStartScreen();
}