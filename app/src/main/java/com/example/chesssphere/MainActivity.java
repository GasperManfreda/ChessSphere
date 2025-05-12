package com.example.chesssphere;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public LinearLayout promotionChoiceLayout;
    private ImageButton promoteToQueenButton;
    private ImageButton promoteToRookButton;
    private ImageButton promoteToBishopButton;
    private ImageButton promoteToKnightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ChessBoardView chessBoardView = findViewById(R.id.chessBoardView);
        ChessGame chessGame = new ChessGame();
        chessBoardView.setGame(chessGame);


        promotionChoiceLayout = findViewById(R.id.promotionChoiceLayout);

        promoteToQueenButton = findViewById(R.id.promoteToQueenButton);
        promoteToRookButton = findViewById(R.id.promoteToRookButton);
        promoteToBishopButton = findViewById(R.id.promoteToBishopButton);
        promoteToKnightButton = findViewById(R.id.promoteToKnightButton);

        promotionChoiceLayout.setBackgroundColor(Color.WHITE);
        promoteToQueenButton.setBackgroundColor(Color.WHITE);
        promoteToKnightButton.setBackgroundColor(Color.WHITE);
        promoteToRookButton.setBackgroundColor(Color.WHITE);
        promoteToBishopButton.setBackgroundColor(Color.WHITE);



    }
}