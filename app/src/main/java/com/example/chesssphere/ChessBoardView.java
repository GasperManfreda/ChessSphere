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
import java.util.ArrayList;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import java.util.List;

public class ChessBoardView extends View {
    private ChessGame game;
    final int PIECE_SIZE = 90;
    final int BOARD_SIZE = 8;
    final int PADDING = 11;

    final int TILE_SIZE = 133;
    private Paint black_paint = new Paint();
    private Paint white_paint = new Paint();
    private Paint textPaint = new Paint();
    private Paint highlightPaint = new Paint();
    private Paint selectedHighlightPaint = new Paint();

    private int selectedRow = -1;
    private int selectedCol = -1;
    private float dragX, dragY;
    private ArrayList<Position> currentPossibleMoves = new ArrayList<>();

    private boolean isDragging = false;
    private Bitmap draggedPieceBitmap = null;
    private float dragOffsetX, dragOffsetY;

    private Piece clickedPiece;

    private int downRow = -1;
    private int downCol = -1;

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

    private Bitmap getBitmapForChesslibPiece(com.github.bhlangonijr.chesslib.Piece piece) {
        if (piece == null || piece == Piece.NONE) return null;

        int colorIndex = (piece.getPieceSide() == Side.WHITE) ? 0 : 1;
        int typeIndex = -1;
        switch (piece.getPieceType()) {
            case PAWN:   typeIndex = 0; break;
            case ROOK:   typeIndex = 1; break;
            case KNIGHT: typeIndex = 2; break;
            case BISHOP: typeIndex = 3; break;
            case QUEEN:  typeIndex = 4; break;
            case KING:   typeIndex = 5; break;
        }
        if (typeIndex != -1 && colorIndex >= 0 && colorIndex < pieceBitmaps.length && typeIndex < pieceBitmaps[colorIndex].length) {
            return pieceBitmaps[colorIndex][typeIndex];
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawHighlights(canvas);
        drawPieces(canvas);

    }

    private void init() {
        game = new ChessGame();
        black_paint.setColor(Color.rgb(125, 84, 41));
        white_paint.setColor(Color.rgb(235, 227, 218));
        textPaint.setTextSize(25);
        textPaint.setAntiAlias(true);


        highlightPaint.setColor(Color.rgb(  128,  128, 128)); // Semi-transparent green
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setAntiAlias(true);


        selectedHighlightPaint.setColor(Color.argb(100, 255, 255, 0)); // Semi-transparent yellow
        selectedHighlightPaint.setStyle(Paint.Style.FILL);
        selectedHighlightPaint.setAntiAlias(true);

    }
    private void drawHighlights(Canvas canvas) {

        if (selectedRow != -1 && selectedCol != -1) {
            int left = selectedCol * TILE_SIZE;
            int top = selectedRow * TILE_SIZE;

            canvas.drawRect(left, top, left + TILE_SIZE, top + TILE_SIZE, selectedHighlightPaint);
        }


        float moveIndicatorRadius = TILE_SIZE / 5f;
        float captureIndicatorStrokeWidth = 5f;
        float captureIndicatorRadius = TILE_SIZE / 2f * 0.8f;


        Paint.Style originalStyle = highlightPaint.getStyle();
        int originalColor = highlightPaint.getColor();
        float originalStrokeWidth = highlightPaint.getStrokeWidth();


        for (Position pos : currentPossibleMoves) {

            float centerX = pos.col * TILE_SIZE + TILE_SIZE / 2f;
            float centerY = pos.row * TILE_SIZE + TILE_SIZE / 2f;


            Piece targetPiece = game.getChesslibPieceAt(pos.row, pos.col);

            if (targetPiece != Piece.NONE) {

                highlightPaint.setStyle(Paint.Style.STROKE);
                highlightPaint.setStrokeWidth(captureIndicatorStrokeWidth);
                highlightPaint.setColor(Color.rgb(  128,  128, 128)); // Semi-transparent red
                canvas.drawCircle(centerX, centerY, captureIndicatorRadius, highlightPaint);
            } else {

                highlightPaint.setStyle(Paint.Style.FILL);
                highlightPaint.setColor(Color.rgb(  128, 128, 128)); // Semi-transparent green
                canvas.drawCircle(centerX, centerY, moveIndicatorRadius, highlightPaint);
            }
        }


        highlightPaint.setStyle(originalStyle);
        highlightPaint.setColor(originalColor);
        highlightPaint.setStrokeWidth(originalStrokeWidth);
    }

    private void drawBoard(Canvas canvas){
        int width = getWidth();
        int height = getHeight();




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
                if (row == selectedRow && col == selectedCol && isDragging) {
                    continue;
                }
                com.github.bhlangonijr.chesslib.Piece chesslibPiece = game.getChesslibPieceAt(row, col);
                if (chesslibPiece != Piece.NONE) {
                     //Bitmap pieceBitmap = getBitmapForChesslibPiece(chesslibPiece);
                     Bitmap scaled_bitmap = Bitmap.createScaledBitmap(getBitmapForChesslibPiece(chesslibPiece),PIECE_SIZE,PIECE_SIZE,true);

                    canvas.drawBitmap(scaled_bitmap, col*TILE_SIZE+20 , row*TILE_SIZE+20, null);
                }
            }
        }
        if (isDragging && draggedPieceBitmap != null) {
            Bitmap scaled_bitmap = Bitmap.createScaledBitmap(draggedPieceBitmap,PIECE_SIZE,PIECE_SIZE,true);
            canvas.drawBitmap(scaled_bitmap, dragX, dragY, null);
        }
    }



    public void setGame(ChessGame game) {
        this.game = game;
    }
    private boolean isValidMoveTarget(int targetRow, int targetCol) {
        for (Position move : currentPossibleMoves) {
            if (move.row == targetRow && move.col == targetCol) {
                return true;
            }
        }
        return false;
    }

    private void selectPiece(int row, int col) {
        selectedRow = row;
        selectedCol = col;

        currentPossibleMoves = (ArrayList<Position>) game.getLegalMoves(selectedRow, selectedCol);

        Log.d("ChessBoardView", "Selected UI (" + selectedRow + "," + selectedCol + "), found moves: " + currentPossibleMoves.size());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        int col = (int) (touchX / TILE_SIZE);
        int row = (int) (touchY / TILE_SIZE);

        boolean onBoard = row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (!onBoard) return false;


                downRow = row;
                downCol = col;
                isDragging = false;


                com.github.bhlangonijr.chesslib.Piece clickedChesslibPiece = game.getChesslibPieceAt(downRow, downCol);

                int clickedPieceColor = (clickedChesslibPiece != com.github.bhlangonijr.chesslib.Piece.NONE) ?
                        (clickedChesslibPiece.getPieceSide() == Side.WHITE ? 0 : 1) : -1;


                if (selectedRow == -1) {
                    if (clickedChesslibPiece != com.github.bhlangonijr.chesslib.Piece.NONE && clickedPieceColor == game.getCurrentPlayer()) {
                        selectPiece(row, col);

                        prepareDrag(clickedChesslibPiece, touchX, touchY);
                    }
                } else {

                    if (row == selectedRow && col == selectedCol) {

                        prepareDrag(clickedChesslibPiece, touchX, touchY);
                        isDragging = false;

                    }else {
                        if (clickedChesslibPiece != com.github.bhlangonijr.chesslib.Piece.NONE && clickedPieceColor == game.getCurrentPlayer()) {
                            selectPiece(row, col);
                            prepareDrag(clickedChesslibPiece, touchX, touchY);
                        }
                    }
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:

                if (selectedRow != -1) {

                    float dx = Math.abs(touchX - (downCol * TILE_SIZE + TILE_SIZE / 2f));
                    float dy = Math.abs(touchY - (downRow * TILE_SIZE + TILE_SIZE / 2f));
                    float dragThreshold = TILE_SIZE / 4f;

                    if (!isDragging && (dx > dragThreshold || dy > dragThreshold)) {

                        isDragging = true;

                        if (draggedPieceBitmap == null && selectedRow != -1) {
                            com.github.bhlangonijr.chesslib.Piece pieceToDrag = game.getChesslibPieceAt(selectedRow, selectedCol);

                        }
                    }

                    if (isDragging) {

                        dragX = touchX - dragOffsetX;
                        dragY = touchY - dragOffsetY;
                        invalidate();
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:

                if (selectedRow != -1) {

                    boolean moveMade = false;
                    int targetRow = row;
                    int targetCol = col;

                    if (onBoard && isValidMoveTarget(targetRow, targetCol)) {
                        moveMade = game.movePiece(selectedRow, selectedCol, targetRow, targetCol);
                        if (moveMade) {
                            clearSelection();
                        }
                    } else {
                        isDragging = false;
                    }

                    invalidate();
                }

                downRow = -1;
                downCol = -1;
                return true;
            default:
                return super.onTouchEvent(event);
        }

    }

    private void prepareDrag(com.github.bhlangonijr.chesslib.Piece piece, float touchX, float touchY) {
        if (piece == null || piece == com.github.bhlangonijr.chesslib.Piece.NONE) {
            this.draggedPieceBitmap = null;
            return;
        }

        this.draggedPieceBitmap = getBitmapForChesslibPiece(piece);



        float bitmapX = selectedCol * TILE_SIZE + (TILE_SIZE - PIECE_SIZE) / 2f;
        float bitmapY = selectedRow * TILE_SIZE + (TILE_SIZE - PIECE_SIZE) / 2f;
        this.dragOffsetX = touchX - bitmapX;
        this.dragOffsetY = touchY - bitmapY;
        this.dragX = touchX - dragOffsetX;
        this.dragY = touchY - dragOffsetY;

    }
    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        currentPossibleMoves.clear();

        isDragging = false;
        draggedPieceBitmap = null;

        downRow = -1;
        downCol = -1;

    }

}
