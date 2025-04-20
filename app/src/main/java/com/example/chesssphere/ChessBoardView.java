package com.example.chesssphere;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ChessBoardView extends View {
    private ChessGame game;
    final int PIECE_SIZE = 90;
    final int BOARD_SIZE = 8;
    final int PADDING = 11;

    final int TILE_SIZE = 133;
    private Paint black_paint = new Paint();
    private Paint white_paint = new Paint();
    private Paint textPaint = new Paint();

    private int selectedRow = -1;  // -1 means no piece selected
    private int selectedCol = -1;
    private float dragX, dragY;    // Current drag position

    private Bitmap[][] pieceBitmaps = new Bitmap[2][6];


    public ChessBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadPieceImages();
    }
    private void loadPieceImages(){

        pieceBitmaps[0][0] = BitmapFactory.decodeResource(getResources(), R.drawable.white_pawn);
        pieceBitmaps[0][1] = BitmapFactory.decodeResource(getResources(), R.drawable.white_rook);
        pieceBitmaps[0][2] = BitmapFactory.decodeResource(getResources(), R.drawable.white_knight);
        pieceBitmaps[0][3] = BitmapFactory.decodeResource(getResources(), R.drawable.white_bishop);
        pieceBitmaps[0][4] = BitmapFactory.decodeResource(getResources(), R.drawable.white_queen);
        pieceBitmaps[0][5] = BitmapFactory.decodeResource(getResources(), R.drawable.white_king);

        pieceBitmaps[1][0] = BitmapFactory.decodeResource(getResources(), R.drawable.black_pawn);
        pieceBitmaps[1][1] = BitmapFactory.decodeResource(getResources(), R.drawable.black_rook);
        pieceBitmaps[1][2] = BitmapFactory.decodeResource(getResources(), R.drawable.black_knight);
        pieceBitmaps[1][3] = BitmapFactory.decodeResource(getResources(), R.drawable.black_bishop);
        pieceBitmaps[1][4] = BitmapFactory.decodeResource(getResources(), R.drawable.black_queen);
        pieceBitmaps[1][5] = BitmapFactory.decodeResource(getResources(), R.drawable.black_king);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
    }

    private void init() {
        game = new ChessGame();
        black_paint.setColor(Color.rgb(125, 84, 41));
        white_paint.setColor(Color.rgb(235, 227, 218));
        textPaint.setTextSize(25);
        textPaint.setAntiAlias(true);

    }

    private void drawBoard(Canvas canvas){
        int width = getWidth();
        int height = getHeight();


        //int tileSize = Math.min(width, height)/BOARD_SIZE;

        for(int row = 0;row < BOARD_SIZE;row ++){
            for(int col = 0;col < BOARD_SIZE; col++) {
                Paint tileColor;

                if ((row + col) % 2 == 0) {
                    tileColor = white_paint;
                } else {
                    tileColor = black_paint;
                }
                int top = col * TILE_SIZE;
                int left = row * TILE_SIZE;

                canvas.drawRect(left, top, left + TILE_SIZE, top + TILE_SIZE, tileColor);

            }
        }
        for(int i = 0; i<BOARD_SIZE; i++){
            String num = String.valueOf(i+1);
            String let = String.valueOf((char)('a'+i));
            if(i%2==0){
                textPaint.setColor(Color.rgb(235, 227, 218));
            }
            else{
                textPaint.setColor(Color.rgb(125, 84, 41));
            }
            canvas.drawText(num, PADDING, TILE_SIZE*7-i*TILE_SIZE+PADDING*3, textPaint);
            canvas.drawText(let, i*TILE_SIZE+TILE_SIZE-PADDING*2, TILE_SIZE*7+PADDING*11, textPaint);

        }
    }

    private void drawPieces(Canvas canvas){
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = game.getBoard().getPiece(row, col);
                if (piece != null) {
                    Bitmap scaled_bitmap = Bitmap.createScaledBitmap(pieceBitmaps[piece.getColor()][piece.getType()],PIECE_SIZE,PIECE_SIZE,true);

                    canvas.drawBitmap(scaled_bitmap, col*TILE_SIZE+20 , row*TILE_SIZE+20, null);
                }
            }
        }
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Select a piece to drag
                int col = (int) (touchX / TILE_SIZE);
                int row = (int) (touchY / TILE_SIZE);
                if (col >= 0 && col < 8 && row >= 0 && row < 8 ) {
                    selectedRow = row;
                    selectedCol = col;
                    dragX = touchX - (TILE_SIZE / 2f);  // Center bitmap on touch
                    dragY = touchY - (TILE_SIZE / 2f);
                    invalidate();  // Redraw with piece at drag position
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                // Update drag position
                if (selectedRow != -1 && selectedCol != -1) {
                    dragX = touchX - (TILE_SIZE / 2f);
                    dragY = touchY - (TILE_SIZE / 2f);
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                // Drop the piece on a new square
                if (selectedRow != -1 && selectedCol != -1) {
                    int newCol = (int) (touchX / TILE_SIZE);
                    int newRow = (int) (touchY / TILE_SIZE);
                    if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8 && game.isLegalMove(game.getBoard().getPiece(selectedRow, selectedCol),newRow,newCol) ) {
                        game.movePiece(selectedRow, selectedCol, newRow, newCol);
                    }
                    else if (selectedRow == newRow && selectedCol == newCol){

                    }

                    selectedRow = -1;  // Reset selection
                    selectedCol = -1;
                    invalidate();  // Redraw at final position
                }
                return true;
        }
        return super.onTouchEvent(event);
    }


}
