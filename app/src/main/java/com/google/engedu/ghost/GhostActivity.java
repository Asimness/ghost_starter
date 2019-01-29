/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String tag = "GhostActivity";
    private  SimpleDictionary sd = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/

        /*
         *  This code creates an InputStream using an assetManager by opening the words.txt file in the assets directory.
         *  Then creates a SimpleDictionary using the InputStream.
         *  If a SimpleDictionary cannot be created it catches the exception and logs it.
         */
        try {
            InputStream fileDescriptor = assetManager.open("words.txt");
            sd = new SimpleDictionary(fileDescriptor);
        }catch(Exception e){
            Log.e(tag, e.toString());
        }
        onStart(null);








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        AssetManager assetManager = getAssets();
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView textView = findViewById(R.id.ghostText);
        // Do computer turn stuff then make it the user's turn again

        String text = textView.getText().toString();
        Log.i(tag, text);
        if(text.length() >= 4){
            if(sd.isWord(text)) {
                label.setText("Computer Wins");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Computer has challenged and won!",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }else{
                // This is as if the computer is challenging the word.
                String possible = sd.getAnyWordStartingWith(text);
                // If no possible word exists the computer calls the user's bluff and wins.
                if(possible == null){
                    label.setText("Computer wins");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Computer has challenged and won!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }
        }
        // test commit
        String newWord;
        newWord = sd.getAnyWordStartingWith(text);
        if(newWord == null){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Error no valid words!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        textView.setText(newWord.substring(0, text.length() + 1));




        userTurn = true;
        label.setText(USER_TURN);
    }


    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        TextView textView = findViewById(R.id.ghostText);
        TextView label = findViewById(R.id.gameStatus);
        Log.i(tag, "s" + keyCode);
        if(keyCode >= 29 || keyCode <= 54){
            char unicodeChar = (char)event.getUnicodeChar();
            String text = textView.getText() + Character.toString(unicodeChar);
            textView.setText(text);

            userTurn = false;
            label.setText("Computer turn");
            computerTurn();

            return true;
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }

    public void restartOnClick(View view){
        this.onStart(view);
    }

    public void challengeOnClick(View view){
        TextView textView = findViewById(R.id.ghostText);
        TextView label = findViewById(R.id.gameStatus);
        String text = textView.getText().toString();

        if(text.length() >=4){
            if(sd.isWord(text)){
                label.setText("User Wins!");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "User Won!",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else{
                String possible = sd.getAnyWordStartingWith(text);
                if(possible == null){
                    label.setText("User Wins!");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "User has challenged and won!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    label.setText("Computer Wins!");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You have lost the challenge and the Computer has won!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }


    }
}
