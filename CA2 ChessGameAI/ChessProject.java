//x18392911 Adam Condon CA2 Intro to AI
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class ChessProject extends JFrame implements MouseListener, MouseMotionListener {

    JLayeredPane layeredPane;
    JPanel chessBoard;
    JLabel chessPiece;
    int xAdjustment;
    int yAdjustment;
    int startX; 
    int startY; 
    int initialX;
    int initialY;
    String promoted;
    JPanel panels;
    JLabel pieces;
    Boolean whiteMove; 
    Boolean possible; 
    String winner; 

    AIAgent agent; 
    Boolean agentWinner; 
    Stack temporary; 

    // For selecting game mode
    private enum GameMode { Random, Next_Best_Move, Two_Levels_Deep } 
    private static GameMode gameMode;

    public static void main(String[] args) {
        ChessProject myChess = new ChessProject();
        myChess.startGame(); 
    }

    //GUI For the Chess Game
    public ChessProject() {
        Dimension boardSize = new Dimension(600, 600);

        // Use a Layered Pane for this application
        layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);

        // Add a chess board to the Layered Pane
        chessBoard = new JPanel();
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
        chessBoard.setLayout(new GridLayout(8, 8));
        chessBoard.setPreferredSize(boardSize);
        chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);

        //for loop for board layout and color of each squares
        for (int i = 0; i < 64; i++) {
            JPanel square = new JPanel(new BorderLayout());
            chessBoard.add(square);

            int row = (i / 8) % 2;
            if (row == 0)
                square.setBackground(i % 2 == 0 ? Color.gray : Color.white);
            else
                square.setBackground(i % 2 == 0 ? Color.white : Color.gray);
        }

        // Setting up the Initial Chess board and the chess pieces
        for (int i = 8; i < 16; i++) {
            pieces = new JLabel(new ImageIcon("WhitePawn.png"));
            panels = (JPanel) chessBoard.getComponent(i);
            panels.add(pieces);
        }
        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
        panels = (JPanel) chessBoard.getComponent(0);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
        panels = (JPanel) chessBoard.getComponent(1);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
        panels = (JPanel) chessBoard.getComponent(6);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteBishup.png"));
        panels = (JPanel) chessBoard.getComponent(2);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteBishup.png"));
        panels = (JPanel) chessBoard.getComponent(5);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKing.png"));
        panels = (JPanel) chessBoard.getComponent(3);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
        panels = (JPanel) chessBoard.getComponent(4);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
        panels = (JPanel) chessBoard.getComponent(7);
        panels.add(pieces);
        for (int i = 48; i < 56; i++) {
            pieces = new JLabel(new ImageIcon("BlackPawn.png"));
            panels = (JPanel) chessBoard.getComponent(i);
            panels.add(pieces);
        }
        pieces = new JLabel(new ImageIcon("BlackRook.png"));
        panels = (JPanel) chessBoard.getComponent(56);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
        panels = (JPanel) chessBoard.getComponent(57);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
        panels = (JPanel) chessBoard.getComponent(62);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackBishup.png"));
        panels = (JPanel) chessBoard.getComponent(58);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackBishup.png"));
        panels = (JPanel) chessBoard.getComponent(61);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKing.png"));
        panels = (JPanel) chessBoard.getComponent(59);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackQueen.png"));
        panels = (JPanel) chessBoard.getComponent(60);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackRook.png"));
        panels = (JPanel) chessBoard.getComponent(63);
        panels.add(pieces);

        possible = false;
        whiteMove = true;
        agent = new AIAgent();
        agentWinner = false;
        temporary = new Stack();
    }

    //Represents a square on the Chess Board
    class Square {
        public int xCoor;
        public int yCoor;
        public String pieceName;

        //Object Class for Squares on the Board
        public Square(int x, int y, String name) {
            xCoor = x;
            yCoor = y;
            pieceName = name;
        }

        public Square(int x, int y) {
            xCoor = x;
            yCoor = y;
            pieceName = "";
        }

        // Returns x coordinate
        public int getXC() {
            return xCoor;
        }

        // Returns y coordinate
        public int getYC() {
            return yCoor;
        }

        // Returns piece name
        public String getName() {
            return pieceName;
        }
    }

    //Move class returning Starting and Landing Squares of AI
    class Move {
        Square start;
        Square landing;

        // Custom Constructor
        public Move(Square x, Square y) {
            start = x;
            landing = y;
        }

        // Default Constructor
        public Move() {

        }

        // Return the starting sqare of the piece
        public Square getStart() {
            return start;
        }

        // Return the landing square of the piece
        public Square getLanding() {
            return landing;
        }
    }

    public class AIAgent {

        //3 Game Methods Implemeted to the Chess Game
            Random rand;
        
            public AIAgent() {
                rand = new Random();
            }
        
            //Random Move (Easy Mode)
            public Move randomMove(Stack possibilities) {
        
                int moveID = rand.nextInt(possibilities.size());
                System.out.println("Agent randomly selected move : " + moveID);
                for (int i = 1; i < (possibilities.size() - (moveID)); i++) {
                    possibilities.pop();
                }

                Move selectedMove = (Move)possibilities.pop();
                return selectedMove;
            }

        //Does not care what happens after the movement. Could take pawn but immediately lose a high ranking piece as a result 
        public Move nextBestMove(Stack whitePossibilties, Stack blackPossibilites) {
            Stack whiteStackM = (Stack) whitePossibilties.clone();
            Stack blackStackM = (Stack) blackPossibilites.clone();
            Move bestNextMove = null;
            Move whiteMove;
            Move presentMove;
            Square blackPos;
            int strengthPiece = 0;
            int strengthChosenPiece = 0; 
    
            // While stack is not empty
            while (!whitePossibilties.empty()) {
                whiteMove = (Move) whitePossibilties.pop();
                presentMove = whiteMove;
    
                // This checks if the centre of the board is occupied by opponents pieces
                // Pawns (and Knight) only pieces able to begin game
                if ((presentMove.getStart().getYC() < presentMove.getLanding().getYC())
                    && (presentMove.getLanding().getXC() == 3) && (presentMove.getLanding().getYC() == 3)
                    || (presentMove.getLanding().getXC() == 4) && (presentMove.getLanding().getYC() == 3)
                    || (presentMove.getLanding().getXC() == 3) && (presentMove.getLanding().getYC() == 4)
                    || (presentMove.getLanding().getXC() == 4) && (presentMove.getLanding().getYC() == 4)) {
                    
                    // Assigning 1 strength to centre pos of board 
                    strengthPiece = 1;
    
                    // Updating best move. Compares the strength of the selected piece and the chosen piece to take
                    if (strengthPiece > strengthChosenPiece) {
                        strengthChosenPiece = strengthPiece;
                        bestNextMove = presentMove;
                    }
                }
    
                // Check white landing pos to black pos, return best attacking move when piece has higher strength than centre
                while (!blackStackM.isEmpty()) {
                    strengthPiece = 0;
                    blackPos = (Square) blackStackM.pop();
                    if ((presentMove.getLanding().getXC() == blackPos.getXC()) && 
                        (presentMove.getLanding().getYC() == blackPos.getYC())) {
    
                        // Assigning strength to pieces
                        if (blackPos.getName().equals("BlackQueen")) {
                            strengthPiece = 9;
                        } else if (blackPos.getName().equals("BlackRook")) {
                            strengthPiece = 5;
                        } else if (blackPos.getName().equals("BlackBishop") || blackPos.getName().equals("BlackKnight")) {
                            strengthPiece = 3;
                        } else if (blackPos.getName().equals("BlackPawn")) {
                            strengthPiece = 1;
                        } else {
                            strengthPiece = 10; // King strength
                        }
                    }
                    // Updates the next best move
                    if (strengthPiece > strengthChosenPiece) {
                        strengthChosenPiece = strengthPiece;
                        bestNextMove = presentMove;
                    }
                }
                // Reloads black squares
                blackStackM = (Stack) blackPossibilites.clone();
            }
    
            // If best next move available then perform move, if not do random move
            if (strengthChosenPiece > 0) {
                System.out.println("Next best move. Opponent piece strength: " + strengthChosenPiece);
                return bestNextMove;
            }        
            return randomMove(whiteStackM);
        }

        //Two Level Deep Not Fully Developed
        //Looks ahead and tries to determine what the player is going to do
        //Extending the nextBestMove using MiniMax routine, Get all possible movements for white and black pieces
        //For each movement the best possible approach needs to be consisered before moving and what could happen after
        //I have ranked the best possible peices and points to get in this method and therefore the highest points will be priortiesed.
        //But only if the player can win or not be beaten by the oponents piece to a certain degree
        
            public Move twoLevelsDeep(Stack whitePossibilties, Stack blackPossibilites) {
                Stack whiteStackM = (Stack) whitePossibilties.clone();
                Stack blackStackM = (Stack) blackPossibilites.clone();
                Move twoLevelsDeep = null;
                Move whiteMove;
                Move blackMove;
                Move presentWhiteMove;
                Move presentBlackMove;
                Square blackPos;
                Square whitePos;
                int whiteStrengthPiece = 0;
                int whiteStrengthChosenPiece = 0;
                int blackStrengthPiece = 0;
                int blackStrengthChosenPiece = 0;
        
                // White possible moves
                while (!whitePossibilties.empty()) {
                    whiteMove = (Move) whitePossibilties.pop();
                    presentWhiteMove = whiteMove;
        
                    // This checks if the centre of the board is occupied by opponents pieces
                    if ((presentWhiteMove.getStart().getYC() < presentWhiteMove.getLanding().getYC())
                            && (presentWhiteMove.getLanding().getXC() == 3) && (presentWhiteMove.getLanding().getYC() == 3)
                            || (presentWhiteMove.getLanding().getXC() == 4) && (presentWhiteMove.getLanding().getYC() == 3)
                            || (presentWhiteMove.getLanding().getXC() == 3) && (presentWhiteMove.getLanding().getYC() == 4)
                            || (presentWhiteMove.getLanding().getXC() == 4) && (presentWhiteMove.getLanding().getYC() == 4)) {

                                whiteStrengthPiece = 0;
                                
                        // Compares the strength of the selected piece and the chosen piece to take
                        if (whiteStrengthPiece > whiteStrengthChosenPiece) {
                            whiteStrengthChosenPiece = whiteStrengthPiece;
                            twoLevelsDeep = presentWhiteMove;
                        }
                    }
        
                    // Check white landing pos to black pos, return best attacking move when piece has higher strength than centre
                    while (!blackStackM.isEmpty()) {
                        whiteStrengthPiece = 0;
                        blackPos = (Square) blackStackM.pop();
                        if ((presentWhiteMove.getLanding().getXC() == blackPos.getXC()) && 
                            (presentWhiteMove.getLanding().getYC() == blackPos.getYC())) {
        
                          // Assigning strength to pieces
                          if (blackPos.getName().equals("BlackQueen")) {
                            whiteStrengthPiece = 9;
                          } else if (blackPos.getName().equals("BlackRook")) {
                            whiteStrengthPiece = 5;
                          } else if (blackPos.getName().equals("BlackBishop") || blackPos.getName().equals("BlackKnight")) {
                            whiteStrengthPiece = 3;
                          } else if (blackPos.getName().equals("BlackPawn")) {
                            whiteStrengthPiece = 1;
                          } else {
                            whiteStrengthPiece = 10; // King strength
                          }
                        }
        
                        // Updates the next best move
                        if (whiteStrengthPiece > whiteStrengthChosenPiece) {
                            whiteStrengthChosenPiece = whiteStrengthPiece;
                            twoLevelsDeep = presentWhiteMove;
                        }
                    }     
                    // Reloads black squares
                    blackStackM = (Stack) blackPossibilites.clone();
                }               

                // Black possible moves
                while (!blackPossibilites.empty()) {
                    blackMove = (Move) blackPossibilites.pop();
                    presentBlackMove = blackMove;
        
                    // This checks if the centre of the board is occupied by opponents pieces
                    if ((presentBlackMove.getStart().getYC() > presentBlackMove.getLanding().getYC())
                            && (presentBlackMove.getLanding().getXC() == 3) && (presentBlackMove.getLanding().getYC() == 3)
                            || (presentBlackMove.getLanding().getXC() == 4) && (presentBlackMove.getLanding().getYC() == 3)
                            || (presentBlackMove.getLanding().getXC() == 3) && (presentBlackMove.getLanding().getYC() == 4)
                            || (presentBlackMove.getLanding().getXC() == 4) && (presentBlackMove.getLanding().getYC() == 4)) {

                                blackStrengthPiece = 0;
                                
                        // Compares the strength of the selected piece and the chosen piece to take
                        if (blackStrengthPiece > blackStrengthChosenPiece) {
                            blackStrengthChosenPiece = blackStrengthPiece;
                            twoLevelsDeep = presentBlackMove;
                        }
                    }
        
                    // Check white landing pos to black pos, return best attacking move when piece has higher strength than centre
                    while (!whiteStackM.isEmpty()) {
                        blackStrengthPiece = 0;
                        whitePos = (Square) whiteStackM.pop();
                        if ((presentBlackMove.getLanding().getXC() == whitePos.getXC()) && 
                            (presentBlackMove.getLanding().getYC() == whitePos.getYC())) {
        
                          // Assigning strength to pieces
                          if (whitePos.getName().equals("WhiteQueen")) {
                            blackStrengthPiece = 9;
                          } else if (whitePos.getName().equals("WhiteRook")) {
                            blackStrengthPiece = 5;
                          } else if (whitePos.getName().equals("WhiteBishop") || whitePos.getName().equals("WhiteKnight")) {
                            blackStrengthPiece = 3;
                          } else if (whitePos.getName().equals("WhitePawn")) {
                            blackStrengthPiece = 1;
                          } else {
                            blackStrengthPiece = 10; // King strength
                          }
                        }
        
                        // Updates the next best move
                        if (blackStrengthPiece > blackStrengthChosenPiece) {
                            blackStrengthChosenPiece = blackStrengthPiece;
                            twoLevelsDeep = presentBlackMove;
                        }
                    }     
                    // Reloads black squares
                    whiteStackM = (Stack) whitePossibilties.clone();
                }

                // If best next move available then perform move, if not do random move
                // If the piece to take is a high ranking piece and the next black move does not have a chance to take our high ranking piece, make the move. Else return
                if (whiteStrengthChosenPiece > 0 && blackStrengthChosenPiece < whiteStrengthChosenPiece) {
                    System.out.println("Selected AI Agent - Two Level Deep: " + whiteStrengthChosenPiece);
                    return twoLevelsDeep;
                }
                return randomMove(whiteStackM);
            }
        }

    //Checks surrounding squares particulary for kings and should return true if no pieces are found
    private Boolean checkSurroundingSquares(Square s) {
        Boolean possible = false;
        int x = s.getXC()*75;
        int y = s.getYC()*75;
        if(!((getPieceName((x+75), y).contains("BlackKing"))||(getPieceName((x-75), y).contains("BlackKing"))
            ||(getPieceName(x,(y+75)).contains("BlackKing"))||(getPieceName((x), (y-75)).contains("BlackKing"))
            ||(getPieceName((x+75),(y+75)).contains("BlackKing"))||(getPieceName((x-75),(y+75)).contains("BlackKing"))
            ||(getPieceName((x+75),(y-75)).contains("BlackKing"))||(getPieceName((x-75), (y-75)).contains("BlackKing")))) {
            possible = true;
        }
        return possible;
    }
    //Get King Squares
    private Stack getKingSquares(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Move validM, validM2, validM3, validM4;
        int tmpx1 = x+1;
        int tmpx2 = x-1;
        int tmpy1 = y+1;
        int tmpy2 = y-1;

        if(!((tmpx1 > 7))){
          Square tmp = new Square(tmpx1, y, piece);
          Square tmp1 = new Square(tmpx1, tmpy1, piece);
          Square tmp2 = new Square(tmpx1, tmpy2, piece);
          if(checkSurroundingSquares(tmp)){
            validM = new Move(startingSquare, tmp);
            if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
              moves.push(validM);
            }
            else{
              if(checkWhiteOponent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
                moves.push(validM);
              }
            }
          }
          if(!(tmpy1 > 7)){
            if(checkSurroundingSquares(tmp1)){
              validM2 = new Move(startingSquare, tmp1);
              if(!piecePresent(((tmp1.getXC()*75)+20), (((tmp1.getYC()*75)+20)))){
                moves.push(validM2);
              }
              else{
                if(checkWhiteOponent(((tmp1.getXC()*75)+20), (((tmp1.getYC()*75)+20)))){
                  moves.push(validM2);
                }
              }
            }
          }
          if(!(tmpy2 < 0)){
            if(checkSurroundingSquares(tmp2)){
              validM3 = new Move(startingSquare, tmp2);
              if(!piecePresent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
                moves.push(validM3);
              }
              else{
                System.out.println("The values that we are going to be looking at are : "
                    +((tmp2.getXC()*75)+20)+" and the y value is : "+((tmp2.getYC()*75)+20));
                if(checkWhiteOponent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
                  moves.push(validM3);
                }
              }
            }
          }
        }
        if(!((tmpx2 < 0))){
          Square tmp3 = new Square(tmpx2, y, piece);
          Square tmp4 = new Square(tmpx2, tmpy1, piece);
          Square tmp5 = new Square(tmpx2, tmpy2, piece);
          if(checkSurroundingSquares(tmp3)){
            validM = new Move(startingSquare, tmp3);
            if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
              moves.push(validM);
            }
            else{
              if(checkWhiteOponent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
                moves.push(validM);
              }
            }
          }
          if(!(tmpy1 > 7)){
            if(checkSurroundingSquares(tmp4)){
              validM2 = new Move(startingSquare, tmp4);
              if(!piecePresent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
                moves.push(validM2);
              }
              else{
                if(checkWhiteOponent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
                  moves.push(validM2);
                }
              }
            }
          }
          if(!(tmpy2 < 0)){
            if(checkSurroundingSquares(tmp5)){
              validM3 = new Move(startingSquare, tmp5);
              if(!piecePresent(((tmp5.getXC()*75)+20), (((tmp5.getYC()*75)+20)))){
                moves.push(validM3);
              }
              else{
                if(checkWhiteOponent(((tmp5.getXC()*75)+20), (((tmp5.getYC()*75)+20)))){
                  moves.push(validM3);
                }
              }
            }
          }
        }
        Square tmp7 = new Square(x, tmpy1, piece);
        Square tmp8 = new Square(x, tmpy2, piece);
        if(!(tmpy1 > 7)){
          if(checkSurroundingSquares(tmp7)){
            validM2 = new Move(startingSquare, tmp7);
            if(!piecePresent(((tmp7.getXC()*75)+20), (((tmp7.getYC()*75)+20)))){
              moves.push(validM2);
            }
            else{
              if(checkWhiteOponent(((tmp7.getXC()*75)+20), (((tmp7.getYC()*75)+20)))){
                moves.push(validM2);
              }
            }
          }
        }
        if(!(tmpy2 < 0)){
          if(checkSurroundingSquares(tmp8)){
            validM3 = new Move(startingSquare, tmp8);
            if(!piecePresent(((tmp8.getXC()*75)+20), (((tmp8.getYC()*75)+20)))){
              moves.push(validM3);
            }
            else{
              if(checkWhiteOponent(((tmp8.getXC()*75)+20), (((tmp8.getYC()*75)+20)))){
                moves.push(validM3);
              }
            }
          }
        }
        return moves;
    }

    //Get Queen Moves
    private Stack getQueenMoves(int x, int y, String piece) {
        Stack completeMoves = new Stack();
        Stack tmpMoves = new Stack();
        Move tmp;

        tmpMoves = getRookMoves(x, y, piece);
        while(!tmpMoves.empty()){
            tmp = (Move)tmpMoves.pop();
            completeMoves.push(tmp);
        }
        tmpMoves = getBishopMoves(x, y, piece);
        while(!tmpMoves.empty()){
            tmp = (Move)tmpMoves.pop();
            completeMoves.push(tmp);
        }
        return completeMoves;
    }

    //Get Rook Moves
    private Stack getRookMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Move validM, validM2, validM3, validM4;

        for(int i=1;i < 8;i++){
            int tmpx = x+i;
            int tmpy = y;
            if(!(tmpx > 7 || tmpx < 0)){
                Square tmp = new Square(tmpx, tmpy, piece);
                validM = new Move(startingSquare, tmp);
                if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
                    moves.push(validM);
                }
                else{
                    if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
                    moves.push(validM);
                    break;
                    }
                    else{
                    break;
                    }
                }
            }
        }
        for(int j=1;j < 8;j++){
            int tmpx1 = x-j;
            int tmpy1 = y;

            if(!(tmpx1 > 7 || tmpx1 < 0)){
                Square tmp2 = new Square(tmpx1, tmpy1, piece);
                validM2 = new Move(startingSquare, tmp2);
                if(!piecePresent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
                    moves.push(validM2);
                }
                else {
                    if(checkWhiteOponent(((tmp2.getXC()*75)+20), ((tmp2.getYC()*75)+20))){
                        moves.push(validM2);
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        for(int k=1;k < 8;k++) {
            int tmpx3 = x;
            int tmpy3 = y+k;

            if(!(tmpy3 > 7 || tmpy3 < 0)){
                Square tmp3 = new Square(tmpx3, tmpy3, piece);
                validM3 = new Move(startingSquare, tmp3);
                if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
                    moves.push(validM3);
                }
                else {
                    if(checkWhiteOponent(((tmp3.getXC()*75)+20), ((tmp3.getYC()*75)+20))){
                        moves.push(validM3);
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        for(int l=1;l < 8;l++) {
            int tmpx4 = x;
            int tmpy4 = y-l;
            if(!(tmpy4 > 7 || tmpy4 < 0)){
                Square tmp4 = new Square(tmpx4, tmpy4, piece);
                validM4 = new Move(startingSquare, tmp4);
                if(!piecePresent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))) {
                    moves.push(validM4);
                }
                else {
                    if(checkWhiteOponent(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20))){
                    moves.push(validM4);
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }
        return moves;
    }

    //Get Bishop Moves
    private Stack getBishopMoves(int x, int y, String piece) {
    Square startingSquare = new Square(x, y, piece);
    Stack moves = new Stack();
    Move validM, validM2, validM3, validM4;
    
    for(int i=1;i < 8;i++){
        int tmpx = x+i;
        int tmpy = y+i;
        if(!(tmpx > 7 || tmpx < 0 || tmpy > 7 || tmpy < 0)){
            Square tmp = new Square(tmpx, tmpy, piece);
            validM = new Move(startingSquare, tmp);
            if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
                moves.push(validM);
            }
            else{
                if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
                    moves.push(validM);
                    break;
                }
                else{
                    break;
                }
            }
        }
    } 
    for(int k=1;k < 8;k++){
        int tmpk = x+k;
        int tmpy2 = y-k;
        if(!(tmpk > 7 || tmpk < 0 || tmpy2 > 7 || tmpy2 < 0)){
            Square tmpK1 = new Square(tmpk, tmpy2, piece);
            validM2 = new Move(startingSquare, tmpK1);
            if(!piecePresent(((tmpK1.getXC()*75)+20), (((tmpK1.getYC()*75)+20)))){
                moves.push(validM2);
            }
            else{
                if(checkWhiteOponent(((tmpK1.getXC()*75)+20), ((tmpK1.getYC()*75)+20))){
                moves.push(validM2);
                    break;
                }
                else{
                    break;
                }
            }
        }
    }
    for(int l=1;l < 8;l++){
        int tmpL2 = x-l;
        int tmpy3 = y+l;
        if(!(tmpL2 > 7 || tmpL2 < 0 || tmpy3 > 7 || tmpy3 < 0)){
            Square tmpLMov2 = new Square(tmpL2, tmpy3, piece);
            validM3 = new Move(startingSquare, tmpLMov2);
            if(!piecePresent(((tmpLMov2.getXC()*75)+20), (((tmpLMov2.getYC()*75)+20)))){
                moves.push(validM3);
            }
            else{
                if(checkWhiteOponent(((tmpLMov2.getXC()*75)+20), ((tmpLMov2.getYC()*75)+20))){
                moves.push(validM3);
                break;
                }
                else{
                break;
                }
            }
        }
    }
    for(int n=1;n < 8;n++){
        int tmpN2 = x-n;
        int tmpy4 = y-n;
        if(!(tmpN2 > 7 || tmpN2 < 0 || tmpy4 > 7 || tmpy4 < 0)){
            Square tmpNmov2 = new Square(tmpN2, tmpy4, piece);
            validM4 = new Move(startingSquare, tmpNmov2);
            if(!piecePresent(((tmpNmov2.getXC()*75)+20), (((tmpNmov2.getYC()*75)+20)))){
                moves.push(validM4);
            }
            else{
                if(checkWhiteOponent(((tmpNmov2.getXC()*75)+20), ((tmpNmov2.getYC()*75)+20))){
                moves.push(validM4);
                break;
                }
                else{
                break;
                }
            }
        }
    }
    return moves;
    }

    //Get Knight Moves
    private Stack getKnightMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x, y, piece);
        Stack moves = new Stack();
        Stack attackingMove = new Stack();
        Square s = new Square(x+1, y+2, piece);
        moves.push(s);
        Square s1 = new Square(x+1, y-2, piece);
        moves.push(s1);
        Square s2 = new Square(x-1, y+2, piece);
        moves.push(s2);
        Square s3 = new Square(x-1, y-2, piece);
        moves.push(s3);
        Square s4 = new Square(x+2, y+1, piece);
        moves.push(s4);
        Square s5 = new Square(x+2, y-1, piece);
        moves.push(s5);
        Square s6 = new Square(x-2, y+1, piece);
        moves.push(s6);
        Square s7 = new Square(x-2, y-1, piece);
        moves.push(s7);

        for(int i=0;i < 8;i++){
            Square tmp = (Square)moves.pop();
            Move tmpmove = new Move(startingSquare, tmp);
            if((tmp.getXC() < 0)||(tmp.getXC() > 7)||(tmp.getYC() < 0)||(tmp.getYC() > 7)){
                
            }
            else if(piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
                if(piece.contains("White")){
                    if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
                        attackingMove.push(tmpmove);
                    }
                }
            }
            else{
                attackingMove.push(tmpmove);
            }
        }
        return attackingMove;
    }

    //Stack of Squares Method
    private void colorSquares(Stack squares) {
        Border greenBorder = BorderFactory.createLineBorder(Color.GREEN, 3);
        while(!squares.empty()){
            Square s = (Square)squares.pop();
            int location = s.getXC() + ((s.getYC())*8);
            JPanel panel = (JPanel)chessBoard.getComponent(location);
            panel.setBorder(greenBorder);
        }
    }
    //Get the Landing Squares
    private void getLandingSquares(Stack found) {
        Move tmp;
        Square landing;
        Stack squares = new Stack();
        while(!found.empty()){
            tmp = (Move)found.pop();
            landing = (Square)tmp.getLanding();
            squares.push(landing);
        }
        colorSquares(squares);
    }
    //Finding White Pieces
    private Stack findWhitePieces() {
        Stack squares = new Stack();
        String icon;
        int x;
        int y;
        String pieceName;
        for(int i=0;i < 600;i+=75){
            for(int j=0;j < 600;j+=75){
                y = i/75;
                x=j/75;
                Component tmp = chessBoard.findComponentAt(j, i);
                if(tmp instanceof JLabel){
                    chessPiece = (JLabel)tmp;
                    icon = chessPiece.getIcon().toString();
                    pieceName = icon.substring(0, (icon.length()-4));
                    if(pieceName.contains("White")){
                        Square stmp = new Square(x, y, pieceName);
                        squares.push(stmp);
                    }
                }
            }
        }
        return squares;
    }
    //Finding Black Pieces
    private Stack findBlackPieces() {
        Stack squares = new Stack();
        String icon;
        int x;
        int y;
        String pieceName;
        for (int i = 0; i < 600; i += 75) {
            for (int j = 0; j < 600; j += 75) {
                y = i / 75;
                x = j / 75;
                Component tmp = chessBoard.findComponentAt(j, i);
                if (tmp instanceof JLabel) {
                    chessPiece = (JLabel) tmp;
                    icon = chessPiece.getIcon().toString();
                    pieceName = icon.substring(0, (icon.length() - 4));
                    if (pieceName.contains("Black")) {
                        Square stmp = new Square(x, y, pieceName);
                        squares.push(stmp);
                    }
                }
            }
        }
        return squares;
    }
    //Checks if a Piece is Present 
    public Boolean piecePresent(int x, int y) {
        Component c = chessBoard.findComponentAt(x, y);
        return !(c instanceof JPanel);
    }

    
    //This method checks if there is a piece present on a particular square.
    
    public Boolean piecePresent(Square square) {
        Component c = chessBoard.findComponentAt((square.getXC() * 75) + 20, (square.getYC() * 75) + 20);
        return !(c instanceof JPanel);
    }
    //Get All White Pawn Moves
    Stack<Move> getWhitePawnMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x ,y ,piece);
        Square downOneSquare = new Square(x, y + 1, piece);
        Square downTwoSquare = new Square(x, y + 2, piece);
        Square leftDiagonalSquare = new Square(x - 1, y + 1, piece);
        Square rightDiagonalSquare = new Square(x + 1, y + 1, piece);

        Stack<Move> moves = new Stack<>();
        boolean isStartingPosition = (startingSquare.getYC() == 1);

        // Check squares below
        if(!piecePresent(downOneSquare)) {
            moves.push(new Move(startingSquare, downOneSquare));

            if(isStartingPosition && !piecePresent(downTwoSquare)) {
                moves.push(new Move(startingSquare, downTwoSquare));
            }
        }

        // Check left diagonal square for opponent
        if(leftDiagonalSquare.getXC() >= 0 && piecePresent(leftDiagonalSquare) && checkWhiteOponent(((leftDiagonalSquare.getXC() * 75) + 20), 
            ((leftDiagonalSquare.getYC() * 75) + 20))) {
            moves.push(new Move(startingSquare, leftDiagonalSquare));
        }

        // Check right diagonal square for opponent
        if(rightDiagonalSquare.getXC() <= 7 && piecePresent(rightDiagonalSquare) && checkWhiteOponent(((rightDiagonalSquare.getXC() * 75) + 20), 
            ((rightDiagonalSquare.getYC() * 75) + 20))) {
            moves.push(new Move(startingSquare, rightDiagonalSquare));
        }

        return moves;
    }
    //Get Black Pawn Moves
    Stack<Move> getBlackPawnMoves(int x, int y, String piece) {
        Square startingSquare = new Square(x ,y ,piece);
        Square upOneSquare = new Square(x, y - 1, piece);
        Square upTwoSquare = new Square(x, y - 2, piece);
        Square leftDiagonalSquare = new Square(x - 1, y - 1, piece);
        Square rightDiagonalSquare = new Square(x + 1, y - 1, piece);

        Stack<Move> moves = new Stack<>();
        boolean isStartingPosition = (startingSquare.getYC() == 6);

        //Check squares above
        if(!piecePresent(upOneSquare)) {
            moves.push(new Move(startingSquare, upOneSquare));

            if(isStartingPosition && !piecePresent(upTwoSquare)) {
                moves.push(new Move(startingSquare, upTwoSquare));
            }
        }

        //Check left diagonal square for opponent
        if(leftDiagonalSquare.getXC() >= 0 && piecePresent(leftDiagonalSquare) && checkBlackOponent(((leftDiagonalSquare.getXC() * 75) + 20), 
            ((leftDiagonalSquare.getYC() * 75) + 20))) {
            moves.push(new Move(startingSquare, leftDiagonalSquare));
        }

        // Check right diagonal square for opponent
        if(rightDiagonalSquare.getXC() <= 7 && piecePresent(rightDiagonalSquare) && checkBlackOponent(((rightDiagonalSquare.getXC() * 75) + 20), 
            ((rightDiagonalSquare.getYC() * 75) + 20))) {
            moves.push(new Move(startingSquare, rightDiagonalSquare));
        }

        return moves;
    }
    //Reset Borders Method
    private void resetBorders() {
        Border empty = BorderFactory.createEmptyBorder();
        for(int i=0;i < 64;i++){
            JPanel tmppanel = (JPanel)chessBoard.getComponent(i);
            tmppanel.setBorder(empty);
        }
    }

    //Making the AI Move
    private void makeAIMove() {
        resetBorders();
        layeredPane.validate();
        layeredPane.repaint();
        Stack white = findWhitePieces();
        Stack black = findBlackPieces();
        Stack completeMoves = new Stack();
        Move tmp;
        while(!white.empty()){
            Square s = (Square)white.pop();
            String tmpString = s.getName();
            Stack tmpMoves = new Stack();
            Stack temporary = new Stack();
            
            //Identifying all the possible moves that can be made by the AI Opponent
            if(tmpString.contains("Knight")){
                tmpMoves = getKnightMoves(s.getXC(), s.getYC(), s.getName());
            }
            else if(tmpString.contains("Bishup")){
                tmpMoves = getBishopMoves(s.getXC(), s.getYC(), s.getName());
            }
            else if(tmpString.contains("WhitePawn")){
                tmpMoves = getWhitePawnMoves(s.getXC(), s.getYC(), s.getName());
            }
            else if(tmpString.contains("BlackPawn")){
                tmpMoves = getBlackPawnMoves(s.getXC(), s.getYC(), s.getName());
            }
            else if(tmpString.contains("Rook")){
              tmpMoves = getRookMoves(s.getXC(), s.getYC(), s.getName());
            }
            else if(tmpString.contains("Queen")){
              tmpMoves = getQueenMoves(s.getXC(), s.getYC(), s.getName());
            }
            else if(tmpString.contains("King")){
                tmpMoves = getKingSquares(s.getXC(), s.getYC(), s.getName());
            }

            while(!tmpMoves.empty()){
            tmp = (Move)tmpMoves.pop();
            completeMoves.push(tmp);
            }
        }
        temporary = (Stack)completeMoves.clone();
        getLandingSquares(temporary);

        //copy of all possible moves = complete moves
        if(completeMoves.size() == 0){
        
        //Stale Mate Position
        JOptionPane.showMessageDialog(null, "Cogratulations, you have placed the AI component in a Stale Mate Position");
        System.exit(0);

        }
        else{
            System.out.println("=============================================================");
            Stack testing = new Stack();            
            while(!completeMoves.empty()){
                Move tmpMove = (Move)completeMoves.pop();
                Square s1 = (Square)tmpMove.getStart();
                Square s2 = (Square)tmpMove.getLanding();
                System.out.println("The "+s1.getName()+" can move from ("+s1.getXC()+", "+s1.getYC()+") to the following square: ("+s2.getXC()+", "+s2.getYC()+")");
                testing.push(tmpMove);
            }
            System.out.println("=============================================================");
            Border redBorder = BorderFactory.createLineBorder(Color.RED, 3);

            //Checks which game mode player selected
            Move selectedMove;
            switch (gameMode) {
                case Random:
                    selectedMove = agent.randomMove(testing);
                    break;
                case Next_Best_Move:
                    selectedMove = agent.nextBestMove(testing, black);
                    break;
                case Two_Levels_Deep:
                    selectedMove = agent.twoLevelsDeep(testing, black);
                    break;
                default:
                    selectedMove = agent.randomMove(testing);
                    break;
            }
          
            Square startingPoint = (Square)selectedMove.getStart();
            Square landingPoint = (Square)selectedMove.getLanding();
            int startX1 = (startingPoint.getXC()*75)+20;
            int startY1 = (startingPoint.getYC()*75)+20;
            int landingX1 = (landingPoint.getXC()*75)+20;
            int landingY1 = (landingPoint.getYC()*75)+20;
            System.out.println("Move "+startingPoint.getName()+" ("+startingPoint.getXC()+", "+startingPoint.getYC()+") to ("+landingPoint.getXC()+", "+landingPoint.getYC()+")");

            Component c  = (JLabel)chessBoard.findComponentAt(startX1, startY1);
            Container parent = c.getParent();
            parent.remove(c);
            int panelID = (startingPoint.getYC() * 8)+startingPoint.getXC();
            panels = (JPanel)chessBoard.getComponent(panelID);
            panels.setBorder(redBorder);
            parent.validate();

            Component l = chessBoard.findComponentAt(landingX1, landingY1);
            if(l instanceof JLabel){
                Container parentlanding = l.getParent();
                JLabel awaitingName = (JLabel)l;
                String agentCaptured = awaitingName.getIcon().toString();

                if(agentCaptured.contains("King")){
                    agentWinner = true;
                }

                parentlanding.remove(l);
                parentlanding.validate();
                pieces = new JLabel( new ImageIcon(startingPoint.getName()+".png") );
                int landingPanelID = (landingPoint.getYC()*8)+landingPoint.getXC();
                panels = (JPanel)chessBoard.getComponent(landingPanelID);
                panels.add(pieces);
                panels.setBorder(redBorder);
                layeredPane.validate();
                layeredPane.repaint();

                if(agentWinner){
                    JOptionPane.showMessageDialog(null, "The AI Agent has won!");
                    System.exit(0);
                }
                
                if (startingPoint.getName().contains("Pawn") && landingPoint.getYC() == 7) {
                    parentlanding.remove(0);
                    pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
                    landingPanelID = (landingPoint.getYC() * 8) + landingPoint.getXC();
                    panels = (JPanel) chessBoard.getComponent(landingPanelID);
                    panels.add(pieces);
                }
            }
            else{
                pieces = new JLabel( new ImageIcon(startingPoint.getName()+".png") );
                int landingPanelID = (landingPoint.getYC()*8)+landingPoint.getXC();
                panels = (JPanel)chessBoard.getComponent(landingPanelID);
                panels.add(pieces);
                panels.setBorder(redBorder);
                layeredPane.validate();
                layeredPane.repaint();
            }
            whiteMove = false;
        }
    }    

    //Get White Pawn Squares
    private Stack getWhitePawnSquares(int x, int y, String piece) {
        Stack moves = new Stack();
        Square startingSquare = new Square(x, y, piece);

        Move validMove1;
        Move validMove2;
        Move validMove3;
        Move validMove4;

        int posx1 = x + 1; // Move one to right
        int posx2 = x - 1; // Move one to left
        int posy1 = y + 1; // Move down one
        int posy2 = y + 2; // Move down two

        Square getSquare1 = new Square(x, posy1, piece); // Pawn moves one square down
        Square getSquare2 = new Square(posx1, posy1, piece); // Pawn moves down one square to the right
        Square getSquare3 = new Square(posx2, posy1, piece); // Pawn moves down one square to the left
        Square getSquare4 = new Square(x, posy2, piece); // Pawn moves two squares down

        if (y == 1) { // If at white pawn starting position
            validMove2 = new Move(startingSquare, getSquare4); // Pawn moves down 2 squares from starting position
            if (!piecePresent(((getSquare4.getXC() * 75) + 20), (((getSquare4.getYC() * 75) + 20))) // If there is not a piece present two squares down
                    && !piecePresent(((getSquare1.getXC() * 75) + 20), (((getSquare1.getYC() * 75) + 20)))) // And no piece one square down
            {
                // push() method is used to Pushes an item onto the top of the stack
                moves.push(startingSquare); // Pushes element into startingSquare stack
            } else {
                // Taking black pieces to the right
                if (!(posx1 > 7)) { // If it is taken a piece that is less than x-axis 7
                    if (piecePresent(((getSquare2.getXC() * 75) + 20), (((getSquare2.getYC() * 75) + 20)))) // If there is a piece present down one square to the right
                    {
                        if (checkWhiteOponent(((getSquare2.getXC() * 75) + 20), (((getSquare2.getYC() * 75) + 20)))) // If it is a black piece
                        {
                            moves.push(validMove2); // Pushes element into validMove2 stack, i.e. takes opponents pawn that is down one square to the right
                        }
                    }
                }

                if (!(posx2 < 0)) { // It it is taken a piece that is greater than x-axis 0
                    if (piecePresent(((getSquare3.getXC() * 75) + 20), (((getSquare3.getYC() * 75) + 20)))) // If there is a piece present down one square to the left
                    {
                        if (checkWhiteOponent(((getSquare3.getXC() * 75) + 20), (((getSquare3.getYC() * 75) + 20)))) // If it is a black piece
                        {
                            moves.push(validMove2); // Pushes element into validMove2 stack, i.e. takes opponents pawn that is down one square to the right
                        }
                    }
                }
            }
        }
        if (!(posy1 > 7)) { // If it is taken a piece that is less than x-axis 7
            validMove1 = new Move(startingSquare, getSquare1);
            if (!piecePresent(((getSquare1.getXC() * 75) + 20), (((getSquare1.getYC() * 75) + 20)))) // If there is not a piece present on square down
            {
                moves.push(validMove1); // Pushes element into validMove1 stack, i.e. moves down one square
            }
            // Taking black pieces to the left
            if (!(posx1 > 7)) {
                validMove3 = new Move(startingSquare, getSquare2);
                if (piecePresent(((getSquare2.getXC() * 75) + 20), (((getSquare2.getYC() * 75) + 20)))) {
                    if (checkWhiteOponent(((getSquare2.getXC() * 75) + 20), (((getSquare2.getYC() * 75) + 20)))) {
                        moves.push(validMove3); // Pushes element into validMove2 stack, i.e. takes opponents pawn that is down one square to the left
                    }
                }
            }
            if (!(posx2 < 0)) {
                validMove4 = new Move(startingSquare, getSquare3);
                if (piecePresent(((getSquare3.getXC() * 75) + 20), (((getSquare3.getYC() * 75) + 20)))) {
                    if (checkWhiteOponent(((getSquare3.getXC() * 75) + 20), (((getSquare3.getYC() * 75) + 20)))) {
                        moves.push(validMove4); // Pushes element into validMove2 stack, i.e. takes opponents pawn that is down one square to the left
                    }
                }
            }
        }
        return moves;
    }

    //Checking for white oponent (Black Pieces) and if they can be taken
    private Boolean checkWhiteOponent(int newX, int newY) {
        Boolean oponent;
        Component c1 = chessBoard.findComponentAt(newX, newY);
        JLabel awaitingPiece = (JLabel) c1;
        String tmp1 = awaitingPiece.getIcon().toString();
        if (((tmp1.contains("Black")))) {
            oponent = true;
        } else {
            oponent = false;
        }
        return oponent;
    }
    
    //Checking for Black oponent (White Pieces) and if they can be taken
    private Boolean checkBlackOponent(int newX, int newY) {
        Boolean oponent;
        Component c1 = chessBoard.findComponentAt(newX, newY);
        JLabel awaitingPiece = (JLabel) c1;
        String tmp1 = awaitingPiece.getIcon().toString();
        if (((tmp1.contains("White")))) {
            oponent = true;
        } else {
            oponent = false;
        }
        return oponent;
    }

    //Checking that player can only take Oponents piece
    private Boolean completeMove(int newX, int newY, String pieceName) {
        Boolean validMove = false;
        System.out.println(pieceName);
        if (piecePresent(newX, newY)) { 
            if (pieceName.contains("White")) { 
                if (checkWhiteOponent(newX, newY)) { 
                    validMove = true; 
                    return validMove;
                } else {
                    validMove = false; 
                    return validMove;
                }
            } else {
                // Check for black piece
                if (checkBlackOponent(newX, newY)) {
                    validMove = true;
                    return validMove;
                } else {
                    validMove = false;
                    return validMove;
                }
            }
        } else {
            validMove = true;
            return validMove;
        }    
    }

    //Piece move method
    private Boolean pieceMove(int x, int y) {
        if ((startX == x) && (startY == y)) {
            return false;
        } else {
            return true;
        }
    }

   //Get Piece Name
    private String getPieceName(int x, int y) {
        Component c1 = chessBoard.findComponentAt(x, y);
        if (c1 instanceof JPanel) {
            return "empty";
        } else if (c1 instanceof JLabel) {
            JLabel awaitingPiece = (JLabel) c1;
            String tmp1 = awaitingPiece.getIcon().toString();
            return tmp1;
        } else {
            return "empty";
        }
    }

    //Checks if the King is Under Threat
    private Boolean kingUnderThreat(int newX, int newY) {
        if ((piecePresent(newX, newY + 75) && getPieceName(newX, newY + 75).contains("King")
                || (piecePresent(newX, newY - 75) && getPieceName(newX, newY - 75).contains("King")))) {
            return true;
        } else if ((piecePresent(newX + 75, newY) && getPieceName(newX + 75, newY).contains("King")
                || (piecePresent(newX - 75, newY) && getPieceName(newX - 75, newY).contains("King")))) {
            return true;
        } else if ((piecePresent(newX - 75, newY + 75) && getPieceName(newX - 75, newY + 75).contains("King")
                || (piecePresent(newX + 75, newY - 75) && getPieceName(newX + 75, newY - 75).contains("King")))) {
            return true;
        } else if ((piecePresent(newX - 75, newY - 75) && getPieceName(newX - 75, newY - 75).contains("King")
                || (piecePresent(newX + 75, newY + 75) && getPieceName(newX + 75, newY + 75).contains("King")))) {
            return true;
        }
        return false;
    }
    //Method is called when a piece is cliked or checks if a peice was clicked
    public void mousePressed(MouseEvent e) {
        chessPiece = null;
        String name = getPieceName(e.getX(), e.getY());
        Component c = chessBoard.findComponentAt(e.getX(), e.getY());
        if (c instanceof JPanel) {
            return;
        }

        Point parentLocation = c.getParent().getLocation();
        xAdjustment = parentLocation.x - e.getX();
        yAdjustment = parentLocation.y - e.getY();
        chessPiece = (JLabel) c;
        initialX = e.getX();
        initialY = e.getY();
        startX = (e.getX() / 75);
        startY = (e.getY() / 75);

        if (name.contains("Knight")) {
            getKnightMoves(startX, startY, name);
        }
        chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
        chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
        layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);
    }

    public void mouseDragged(MouseEvent me) {
        if (chessPiece == null)
            return;
        chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
    }

    //This method is put in place for releasing the mouse and a valid move was made
    public void mouseReleased(MouseEvent e) {
        if (chessPiece == null) {
            return;
        }
        chessPiece.setVisible(false);
        Boolean success = false;
        Boolean promotion = false;
        Boolean progression = false;
        Component c = chessBoard.findComponentAt(e.getX(), e.getY());

        String tmp = chessPiece.getIcon().toString();
        String pieceName = tmp.substring(0, (tmp.length() - 4));
        Boolean validMove = false;

        int landingX = (e.getX() / 75);
        int landingY = (e.getY() / 75);

        int xMovement = Math.abs((e.getX() / 75) - startX);
        int yMovement = Math.abs((e.getY() / 75) - startY);
        System.out.println("----------------------------------------------");
        System.out.println("Piece being moved is : " + pieceName);
        System.out.println("Starting coordinates are : " + "( " + startX + "," + startY + ")");
        System.out.println("xMovement is : " + xMovement);
        System.out.println("yMovement is : " + yMovement);
        System.out.println("Landing coordinates are : " + "( " + landingX + "," + landingY + ")");
        System.out.println("----------------------------------------------");

        Boolean possible = false;

       //Ensures white moves first and then black in this order 
        if (whiteMove) {
            if (pieceName.contains("White")) {
                possible = true;
            }
        } else if (pieceName.contains("Black")) {
            possible = true;
        }

        //Get all King Movements
        // If it is the correct players turn
        if (possible) {
            // If the piece that is selected is a King
            if (pieceName.contains("King")) {
                if ((xMovement == 0) && (yMovement == 0)) { // King did not move
                    validMove = false;
                } else if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) { // King moved off the board
                    validMove = false; 
                } else if ((xMovement > 1) || (yMovement > 1)) { // If King moved greater than one square
                    validMove = false; 
                  // Checks if the opponents King is beside the square the player has chosen to place his King  
                } else if ((getPieceName((e.getX() + 75), e.getY()).contains("King"))
                        || (getPieceName((e.getX() - 75), e.getY()).contains("King"))
                        || (getPieceName((e.getX()), (e.getY() + 75)).contains("King"))
                        || (getPieceName((e.getX()), (e.getY() - 75)).contains("King"))
                        || (getPieceName((e.getX() + 75), (e.getY() + 75)).contains("King"))
                        || (getPieceName((e.getX() - 75), (e.getY() + 75)).contains("King"))
                        || (getPieceName((e.getX() + 75), (e.getY() - 75)).contains("King"))
                        || (getPieceName((e.getX() - 75), (e.getY() - 75)).contains("King"))) {
                    validMove = false;
                } else if (piecePresent(e.getX(), e.getY())) { 
                    if (pieceName.contains("White")) { 
                        if (checkWhiteOponent(e.getX(), e.getY())) {
                            validMove = true; 
                        }
                    } else if (checkBlackOponent(e.getX(), e.getY())) {
                        validMove = true;
                    }
                } else {
                    validMove = true;
                }
            }
            //Queen Movements
            else if (pieceName.contains("Queen")) {
                boolean inTheWay = false;
                if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) { // Did not move off board
                    validMove = false;
                } else if (!pieceMove(landingX, landingY)) { // If the landing pos are not the pieceMove function
                    validMove = false;
                } else if (xMovement == yMovement) { // BISHOP STYLE MOVES
                    if ((startX - landingX < 0) && (startY - landingY < 0)) { // Moved diagonally left and down
                        for (int i = 0; i < xMovement; i++) {
                            if (piecePresent((initialX + (i * 75)), (initialY + (i * 75)))) {
                                inTheWay = true;
                            }
                        }
                    } else if ((startX - landingX < 0) && (startY - landingY > 0)) { // Moved diagonally left and up
                        for (int i = 0; i < xMovement; i++) {
                            if (piecePresent((initialX + (i * 75)), (initialY - (i * 75)))) {
                                inTheWay = true;
                            }
                        }
                    } else if ((startX - landingX > 0) && (startY - landingY > 0)) { // Moved diagonally right and up
                        for (int i = 0; i < xMovement; i++) {
                            if (piecePresent((initialX - (i * 75)), (initialY - (i * 75)))) {
                                inTheWay = true;
                            }
                        }
                    } else if ((startX - landingX > 0) && (startY - landingY < 0)) { 
                        for (int i = 0; i < xMovement; i++) {
                            if (piecePresent((initialX - (i * 75)), (initialY + (i * 75)))) {
                                inTheWay = true;
                            }
                        }
                    }
                    if (inTheWay) {
                        validMove = false;
                    } else if (piecePresent(e.getX(), (e.getY()))) { 
                        if (pieceName.contains("White")) {
                            if (checkWhiteOponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) { 
                                    winner = "The White AI has won the game!"; 
                                }
                            }
                        } else {
                            if (checkBlackOponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) { 
                                    winner = "The Black Player has won the game!";
                                }
                            }
                        }
                    } else {
                        validMove = true;
                    }
                } else if (((xMovement != 0) && (yMovement == 0)) || ((xMovement == 0) && (yMovement != 0))) { 
                    if (xMovement != 0) {
                        if (startX - landingX > 0) { 
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent(initialX - (i * 75), e.getY())) {
                                    inTheWay = true;
                                    break;
                                } else {
                                    inTheWay = false;
                                }
                            }
                        } else {
                            for (int i = 0; i < xMovement; i++) { 
                                if (piecePresent(initialX + (i * 75), e.getY())) {
                                    inTheWay = true;
                                    break;
                                } else {
                                    inTheWay = false;
                                }
                            }
                        }
                    } else {
                        if (startY - landingY > 0) {
                            for (int i = 0; i < yMovement; i++) { 
                                if (piecePresent(e.getX(), initialY - (i * 75))) {
                                    inTheWay = true;
                                    break;
                                } else {
                                    inTheWay = false;
                                }
                            }
                        } else {
                            for (int i = 0; i < yMovement; i++) { 
                                if (piecePresent(e.getX(), initialY + (i * 75))) {
                                    inTheWay = true;
                                    break;
                                } else {
                                    inTheWay = false;
                                }
                            }
                        }
                    }
                    if (inTheWay) {
                        validMove = false;
                    } else if (piecePresent(e.getX(), (e.getY()))) {
                        if (pieceName.contains("White")) {
                            if (checkWhiteOponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "The White AI has won the game!";
                                }
                            }
                        } else if (checkBlackOponent(e.getX(), e.getY())) {
                            validMove = true;
                            if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                winner = "The Black Player has won the game!";
                            }
                        }
                    } else {
                        validMove = true;
                    }
                }
            }
            //Knight Movements
            else if (pieceName.contains("Knight")) {
                if (((xMovement == 1) && (yMovement == 2)) || ((xMovement == 2) && (yMovement == 1))) {
                    if (!piecePresent(e.getX(), e.getY())) { 
                        validMove = true;
                    } else if (pieceName.contains("White")) { 
                        if (checkWhiteOponent(e.getX(), e.getY())) {
                            validMove = true;
                            if (getPieceName(e.getX(), e.getY()).contains("King")) { 
                                winner = "The White AI has won the game!";
                            }
                        }
                    } else if (checkBlackOponent(e.getX(), e.getY())) {
                        validMove = true;
                        if (getPieceName(e.getX(), e.getY()).contains("King")) { 
                            winner = "The Black Player has won the game!";
                        }
                    }
                }
            }
            //Rook Movements
            else if (pieceName.contains("Rook")) {
                Boolean intheway = false;
                if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) { // Did move off the board
                    validMove = false;
                } else if (!pieceMove(landingX, landingY)) {
                    validMove = false;
                } else if (((xMovement != 0) && (yMovement == 0)) || ((xMovement == 0) && (yMovement != 0))) { // Moved left/ right OR up/down
                    if (xMovement != 0) { 
                        if (startX - landingX > 0) { // Moved left
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent(initialX - (i * 75), e.getY())) {
                                    intheway = true;
                                    break;
                                } else {
                                    intheway = false;
                                }
                            }
                        } else {
                            for (int i = 0; i < xMovement; i++) { // Moved right
                                if (piecePresent(initialX + (i * 75), e.getY())) {
                                    intheway = true;
                                    break;
                                } else {
                                    intheway = false;
                                }
                            }
                        }
                    } else {
                        if (startY - landingY > 0) { // Moved up
                            for (int i = 0; i < yMovement; i++) { 
                                if (piecePresent(e.getX(), initialY - (i * 75))) {
                                    intheway = true;
                                    break;
                                } else {
                                    intheway = false;
                                }
                            }
                        } else {
                            for (int i = 0; i < yMovement; i++) { // Moved down
                                if (piecePresent(e.getX(), initialY + (i * 75))) {
                                    intheway = true;
                                    break;
                                } else {
                                    intheway = false;
                                }
                            }
                        }
                    }
                    if (intheway) {
                        validMove = false; 
                    } else if (piecePresent(e.getX(), (e.getY()))) {
                        // Taking opponents piece
                        if (pieceName.contains("White")) {
                            if (checkWhiteOponent(e.getX(), e.getY())) {
                                validMove = true;

                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "The White AI has won the game!";
                                }
                            } else {
                                validMove = false;
                            }
                        } else if (checkBlackOponent(e.getX(), e.getY())) {
                            validMove = true;
                            if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                winner = "The Black Player has won the game!";
                            }
                        } else {
                            validMove = false;
                        }
                    } else {
                        validMove = true;
                    }
                } else {
                    validMove = false;
                }
            }
            //Bishop Movements
            else if (pieceName.contains("Bishup")) {
                Boolean inTheWay = false;
                if (((landingX < 0) || (landingX > 7)) || ((landingY < 0) || (landingY > 7))) { // Did move off the board
                    validMove = false;
                } else if (!pieceMove(landingX, landingY)) {
                    validMove = false;
                } else {
                    validMove = true;

                    if (xMovement == yMovement) {
                        if ((startX - landingX < 0) && (startY - landingY < 0)) { // Moved diagonally left and down
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX + (i * 75)), (initialY + (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX < 0) && (startY - landingY > 0)) { // Moved diagonally left and up
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX + (i * 75)), (initialY - (i * 75)))) {
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX > 0) && (startY - landingY > 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX - (i * 75)), (initialY - (i * 75)))) { // Moved diagonally right and up
                                    inTheWay = true;
                                }
                            }
                        } else if ((startX - landingX > 0) && (startY - landingY < 0)) {
                            for (int i = 0; i < xMovement; i++) {
                                if (piecePresent((initialX - (i * 75)), (initialY + (i * 75)))) { // Moved diagonally right and down
                                    inTheWay = true;
                                }
                            }
                        }
                        if (inTheWay) {
                            validMove = false;
                        } else if (piecePresent(e.getX(), (e.getY()))) {
                            // Taking opponents piece
                            if (pieceName.contains("White")) {
                                if (checkWhiteOponent(e.getX(), e.getY())) {
                                    validMove = true;
                                    if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                        winner = "The White AI has won the game!";
                                    }
                                } else {
                                    validMove = false;
                                }
                            } else if (checkBlackOponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "The Black Player has won the game!";
                                }
                            } else {
                                validMove = false;
                            }
                        } else {
                            validMove = true;
                        }
                    } else {
                        validMove = false;
                    }
                }
            }
            //Black Pawn Movements
            else if (pieceName.equals("BlackPawn")) {
                if (startY == 6) { 
                    if (((yMovement == 1) || (yMovement == 2)) && (startY > landingY) && (xMovement == 0)) {
                        if (yMovement == 2) { 
                            if ((!piecePresent(e.getX(), e.getY())) && (!piecePresent(e.getX(), (e.getY() + 75)))) {
                                validMove = true; 
                            }
                        } else if (!piecePresent(e.getX(), e.getY())) {
                            validMove = true;
                        }
                    } else if ((yMovement == 1) && (startY > landingY) && (xMovement == 1)) {
                        if (piecePresent(e.getX(), e.getY())) {
                            if (checkBlackOponent(e.getX(), e.getY())) { 
                                validMove = true;
                                if (getPieceName(e.getX(), e.getY()).contains("King")) { 
                                    winner = "The Black Player has won the game!";
                                }
                            }
                        }
                    }
                } else {
                    // Moves only one square up from starting position
                    if (((yMovement == 1)) && (startY > landingY) && (xMovement == 0)) {
                        if (!piecePresent(e.getX(), e.getY())) {
                            validMove = true;

                            if (landingY == 0) {
                                progression = true;
                            }
                        }
                    } else if ((yMovement == 1) && (startY > landingY) && (xMovement == 1)) {
                        if (piecePresent(e.getX(), e.getY())) {
                            if (checkBlackOponent(e.getX(), e.getY())) {
                                validMove = true;
                                if (landingY == 0) {
                                    progression = true;
                                }
                                if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                    winner = "The Black Player has won the game!";
                                }
                            }
                        }
                    }
                }
            }
            //White Pawn Movements 
            else if (pieceName.equals("WhitePawn")) {
                if (startY == 1) {
                    if (((xMovement == 0)) && ((yMovement == 1) || ((yMovement) == 2))) {
                        if (yMovement == 2) {
                            if ((!piecePresent(e.getX(), (e.getY()))) && (!piecePresent(e.getX(), (e.getY() - 75)))) {
                                validMove = true;
                            }
                        } else if ((!piecePresent(e.getX(), (e.getY())))) {
                            validMove = true;
                        }
                    } else if ((piecePresent(e.getX(), e.getY())) && (xMovement == yMovement) && (xMovement == 1)
                            && (startY < landingY)) {
                        if (checkWhiteOponent(e.getX(), e.getY())) {
                            validMove = true;
                            if (startY == 6) {
                                success = true;
                            }
                            if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                winner = "The White AI has won the game!";
                            }
                        }
                    }
                } else if ((startX - 1 >= 0) || (startX + 1 <= 7)) {
                    if ((piecePresent(e.getX(), e.getY())) && (xMovement == yMovement) && (xMovement == 1)) {
                        if (checkWhiteOponent(e.getX(), e.getY())) {
                            validMove = true;
                            if (startY == 6) {
                                success = true;
                            }
                            if (getPieceName(e.getX(), e.getY()).contains("King")) {
                                winner = "The White AI has won the game!";
                            }
                        }
                    } else if (!piecePresent(e.getX(), (e.getY()))) {
                        if (((xMovement == 0)) && ((e.getY() / 75) - startY) == 1) {
                            if (startY == 6) {
                                success = true;
                            }
                            validMove = true;
                        }
                    }
                }
            }
        }

        if (!validMove) {
            int location = 0;
            if (startY == 0) {
                location = startX;
            } else {
                location = (startY * 8) + startX;
            }
            String pieceLocation = pieceName + ".png";
            pieces = new JLabel(new ImageIcon(pieceLocation));
            panels = (JPanel) chessBoard.getComponent(location);
            panels.add(pieces);
        } else {
            if (whiteMove) {
                whiteMove = false;
            } else {
                whiteMove = true;
            }

            if (progression) {
                //Select promotion
                int location = 0 + (e.getX() / 75);
                if (c instanceof JLabel) {
                    promoted = JOptionPane.showInputDialog(null,"Please Enter the Piece Wanted in this format color + piece name: 'BlackQueen' ");
                    if(promoted.equals("BlackQueen")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("BlackQueen.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("BlackKing")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("BlackKing.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("BlackBishup")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("BlackBishup.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("BlackRook")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("BlackRook.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("BlackKnight")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                } else {
                    Container parent = (Container) c;
                    pieces = new JLabel(new ImageIcon("BlackQueen.png"));
                    parent = (JPanel) chessBoard.getComponent(location);
                    parent.add(pieces);
                }
                if (winner != null) {
                    JOptionPane.showMessageDialog(null, winner);
                    System.exit(0);
                }
            } else if (success) {
                int location = 56 + (e.getX() / 75);
                if (c instanceof JLabel) {
                    promoted = JOptionPane.showInputDialog(null,"Please Enter the Piece Wanted in this format: 'WhiteQueen' ");
                    if(promoted.equals("WhiteQueen")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("WhiteBishup")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("WhiteBishup.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("WhiteKing")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("WhiteKing.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("WhiteKnight")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                    if(promoted.equals("WhiteRook")){
                        Container parent = c.getParent();
                        parent.remove(0);
                        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
                        parent = (JPanel) chessBoard.getComponent(location);
                        parent.add(pieces);
                    }
                } else {
                    Container parent = (Container) c;
                    pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
                    parent = (JPanel) chessBoard.getComponent(location);
                    parent.add(pieces);
                }
            } else {
                if (c instanceof JLabel) {
                    Container parent = c.getParent();
                    parent.remove(0);
                    parent.add(chessPiece);
                } else {
                    Container parent = (Container) c;
                    parent.add(chessPiece);
                }
                chessPiece.setVisible(true);
                if (winner != null) {
                    JOptionPane.showMessageDialog(null, winner);
                    System.exit(0);
                }
            }
            makeAIMove();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    
    // Main method that gets the ball moving.
    public void startGame() {
        ChessProject frame = new ChessProject();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Object[] mode = {"Random Moves (Easy)", "Best Next Move (Hard)", "Based on Opponents Moves (Expert)"};
        int gameOption = JOptionPane.showOptionDialog(frame, "Please select an AI opponent to play against", "AI CA2",
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, mode, mode[2]);

        switch (gameOption) {
            case 0:
                gameMode = GameMode.Random;
                break;
            case 1:
                gameMode = GameMode.Next_Best_Move;
                break;
            case 2:
                gameMode = GameMode.Two_Levels_Deep;
                break;
        }

        System.out.println("The selected game mode is : " + gameMode.toString());
        frame.makeAIMove();
    }
}
