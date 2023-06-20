package projeto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

public class PrimaryController implements Initializable {
    @FXML
    private Label messageLabel;

    @FXML
    private GridPane gridPane;
    @FXML

    private Pane innerPane;
    @FXML
    
    

    private Button[][] chessboard;

    private Piece[][] boardState;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private String playerColor="white";
    private String chessColor="";
    private static Socket socket;


    

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       
        int size = 8; // Tamanho do tabuleiro (8x8)

        chessboard = new Button[size][size]; // Inicia o array
        boardState = new Piece[size][size]; // Inicializa o estado do tabuleiro


        // Cria botões para cada célula do tabuleiro
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Button button = new Button();
                button.setPrefSize(50, 50); // Define o tamanho preferencial do botão
                button.setStyle("-fx-background-color: " + getCellColor(row, col) + ";"); // Define a cor do botão com base na posição da célula
                button.setId("button_" + row + "_" + col); // Atribui um ID único com base nas posições da linha e coluna
                button.setOnMouseClicked(this::handleButtonClicked);
                

                chessboard[row][col] = button; // Armazena o botão no array

                gridPane.add(button, col, row); // Adiciona o botão ao gridPane na linha e coluna especificadas
                
            }
        }
    }

    public void togglePane() {
        innerPane.setVisible(!innerPane.isVisible());
    }
   
        public void finishProgram() {
            System.exit(0);
    }
   

      private void connectToServer() {
        try {
            String serverAddress = "127.0.0.1"; // Atualize com o endereço IP do servidor
            int serverPort = 5000; // Atualize com a porta do servidor
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + socket.getInetAddress());
            InputStream inputStream = socket.getInputStream();

            // Cria um novo tópico para ouvir as mensagens recebidas
               Thread listenerThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    
                    // Lê continuamente a partir do fluxo de entrada até chegar ao fim
                    while((bytesRead = inputStream.read(buffer)) != -1){
                        // Converte os bytes recebidos numa string
                        String receivedMessage = new String(buffer, 0, bytesRead);
                        
                        // Se a cor estiver vazia, define a mesma como "preta"
                        if(chessColor.equals(""))
                            chessColor="black";
                        // Executa o seguinte código no encadeamento do aplicativo JavaFX para manipular a mensagem recebida
                       Platform.runLater(()->{    handleMessages(receivedMessage);});

                       // Imprime a mensagem na consola
                        System.out.println("Recebido: clientside " + receivedMessage);
                        
                    }
                } catch (IOException e) {
                    // Lida com qualquer exceção de IO que ocorrem durante o processo de leitura
                }
            });
               // "Ouve" as mensagens recebidas
            listenerThread.start();
        } catch (IOException e) {
        }
    }


    private String getCellColor(int row, int col) {
        // Determina a cor da célula com base nas posições de linha e coluna
        boolean isEvenRow = row % 2 == 0;
        boolean isEvenCol = col % 2 == 0;

        // Alterna entre preto e branco para cada célula
        if ((isEvenRow && isEvenCol) || (!isEvenRow && !isEvenCol)) {
            return "white";
        } else {
            return "black";
        }
    }
    
   public void WinCheck() {
    int blackCount = 0;
    int whiteCount = 0;
    
    // Percorra as linhas e colunas do tabuleiro
    for (int row = 0; row < 3; row++) {
        for (int col = 0; col < chessboard[row].length; col++) {
            // Verifique se há uma peça no estado do tabuleiro
            if (boardState[row][col] != null) {
                // Verifique se a cor da célula é "preta"
                if ("black".equals(boardState[row][col].getColor())) {
                    blackCount++;
                }
                // Verifique se a cor da célula é "branca"
                if ("white".equals(boardState[row][col].getColor())) {
                    whiteCount++;
                }
            }
        }
    }

    // Verifica as celulas
   /* if (whiteCount == 0) {
        messageLabel.setText("Derrota!");
    }
    // Verifica as celulas
    if (blackCount == 0) {
        messageLabel.setText("Vitória!");
    } */
}


    public void startGame() {
        connectToServer(); // Conecta ao servidor
        clearBoard(); // Limpa todas as peças existentes no tabuleiro
        

        // Coloca peças nas células pretas
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < chessboard[row].length; col++) {
                if ((row + col) % 2 == 0) {
                    addPieceToButton(chessboard[row][col], "black"); // adicionar peça às celulas pretas
                    boardState[row][col] = new Piece("regular","black"); // Cria uma nova peça e a armazena no array boardState
                }
            }
        }

        // Coloca peças nas células brancas
        for (int row = chessboard.length - 1; row > chessboard.length - 4; row--) {
            for (int col = 0; col < chessboard[row].length; col++) {
                if ((row + col) % 2 == 0) {
                    addPieceToButton(chessboard[row][col], "white"); // adicionar peça às celulas brancas
                    boardState[row][col] = new Piece("regular","white"); // Cria uma nova peça e a armazena no array boardState
                }
            }
        }
    }

    private void addPieceToButton(Button button, String color) {
        Circle circle = new Circle(20); // Cria um círculo com raio 20 para representar a peça
        // Set the fill color of the circle based on the piece's color
        if (color.equals("black")) {
            circle.setFill(Color.RED);
        } else if (color.equals("white")) {
            circle.setFill(Color.GREEN);
        }
        
        /*
        if (boardState[selectedRow][selectedCol].getType().equals("dama")) {
            circle.setFill(color.equals("black") ? Color.DARKRED : Color.DARKGREEN);
        } else {
            circle.setFill(color.equals("black") ? Color.RED : Color.GREEN);
        }
        
        */
        
        circle.setStroke(Color.BLACK); // Define a cor da borda do círculo como preto
        
        
        button.setGraphic(circle); // Define o círculo como o elemento gráfico do botão
}


    private void clearBoard() {
    for (Button[] row : chessboard) {
        for (Button button : row) {
            button.setGraphic(null); // Remove a representação gráfica associada ao botão
        }
    }
    resetSelection(); // Redefine qualquer seleção ou estado de destaque no jogo
}

    private void resetSelection() {
        selectedRow = -1; // Redefine o índice da linha selecionada para -1
        selectedCol = -1; // Redefine o índice da coluna selecionada para -1
    }

    public void showDescription(MouseEvent event) {
        innerPane.setVisible(true); // Torna o painel interno visível quando ocorre o MouseEvent
    }


    private void handleButtonClicked(MouseEvent event) {
    if (chessColor.equals("")) {
        chessColor = "white"; // Define a cor da celula como branca caso não tenha sido definida anteriormente
    }
    
    if (playerColor.equals(chessColor)) { // Verifica se a cor do jogador é igual à cor da peça atual
        Button button = (Button) event.getSource(); // Obtém o botão clicado
        String[] idParts = button.getId().split("_"); // Divide o ID do botão em partes
        int row = Integer.parseInt(idParts[1]); // Obtém o índice da linha a partir do ID
        int col = Integer.parseInt(idParts[2]); // Obtém o índice da coluna a partir do ID

        sendMessageToServer("localhost", 5000, row + " " + col); // Envia uma mensagem ao servidor com a posição do botão clicado
    }
    
    WinCheck();
}

    
    private void handleMessages(String message) {
    String[] tokens = message.split(" ");
    
    if (tokens.length != 2) {
        System.out.println("Erro do servidor!"); // Exibe uma mensagem de erro caso a mensagem do servidor não esteja no formato esperado
    } else {
        int cols = Integer.parseInt(tokens[1]); // Obtém a coluna do movimento recebido do servidor
        int row = Integer.parseInt(tokens[0]); // Obtém a linha do movimento recebido do servidor
        System.out.println("row: " + row + ", cols: " + cols);

        if (selectedRow == -1 && selectedCol == -1) {
            // Se não houver seleção anterior, seleciona o botão atual se ele contiver uma peça da cor do jogador
            String pieceColor = boardState[row][cols].getColor();
            if (pieceColor != null && pieceColor.equals(playerColor)) {
                selectPiece(row, cols); // Seleciona a peça na posição (row, cols)
            }
        } else {
            // Se houver uma seleção anterior, tenta mover a peça selecionada para o botão atual
            moveSelectedPiece(row, cols); // Move a peça selecionada para a posição (row, cols)
        }
    }
}


    private void selectPiece(int row, int col) {
        selectedRow = row; // Linha selecionada
        selectedCol = col; // Coluna selecionada
    }

    private void moveSelectedPiece(int targetRow, int targetCol) {
        // Atualize o estado do tabuleiro
          placePiece(targetRow,targetCol);
        // Limpa a seleção depois da jogada
        resetSelection();
    }
    
    private boolean isValidMove(int sourceRow, int sourceCol, int targetRow, int targetCol) {
     
    // Verifica se a posição do alvo está dentro dos limites do tabuleiro
        if (targetRow < 0 || targetRow >= chessboard.length || targetCol < 0 || targetCol >= chessboard[targetRow].length) {
            return false;
        }

    // Verifica se a posição de destino está vazia
        if (boardState[targetRow][targetCol] != null) {
            return false;
        }

    // Determina a direção do movimento com base na cor da peça
        int direction = (playerColor.equals("black")) ? 1 : -1; 
    

    // Verifica se o movimento é diagonal
        int rowDiff = targetRow - sourceRow;
        int colDiff = Math.abs(targetCol - sourceCol);
    // Verifica se a peça está a ser movida na direção correta
        if (direction == 1 && rowDiff < 0) {
        if("regular".equals(boardState[selectedRow][selectedCol].getType()))
        {return false;}
    }

        if (direction == -1 && rowDiff > 0) {
            if("regular".equals(boardState[selectedRow][selectedCol].getType()))
        {return false;}
    }

     // Verifica se a peça foi capturada

       if (Math.abs(rowDiff)==2&&Math.abs(colDiff)==2) {
        int capturedRow = (sourceRow + targetRow) / 2; // Linha da peça capturada
        int capturedCol = (sourceCol + targetCol) / 2; // Coluna da peça capturada

        String opponentColor = (playerColor.equals("black")) ? "white" : "black";
        if (boardState[capturedRow][capturedCol] != null && boardState[capturedRow][capturedCol].getColor().equals(opponentColor)) {
            // A captura é válida, remove a peça capturada do tabuleiro
            removePiece(capturedRow, capturedCol);
            return true;
        } else {
            // Não há peças do oponente para capturar
            return false;
        }
    }
        if (Math.abs(rowDiff) != 1 || colDiff !=1) {
            return false;
        }
    
    // Jogada sem capturar
        return true;
    }

    private void removePiece(int row, int col) {
        Button button = chessboard[row][col]; // Posição da peça
        button.setGraphic(null); // Remove qualquer grafico associado ao botao
        boardState[row][col] = null; // Limpa a posição
    }

    
   private void placePiece(int row, int col) {
    Button button = chessboard[row][col];

    // Determina a cor da peça com base na lógica do jogo
    String pieceColor = playerColor;

    // Verifica se a jogada é válida
    if (isValidMove(selectedRow, selectedCol, row, col)) {
        // Adiciona peça ao botão
        addPieceToButton(button, pieceColor);
        // Atualiza estado do tabuleiro
        boardState[row][col]=new Piece(boardState[selectedRow][selectedCol].getType(),pieceColor);
        // Retira a peça da posição anterior

        if(row==7&&pieceColor.equals("black"))
        {
        boardState[row][col].setType("dama"); // muda o tipo para dama quando chega aao outro lado do tabuleiro
        
        }
         if(row==0&&pieceColor.equals("white"))
        {
        boardState[row][col].setType("dama"); // muda o tipo para dama quando chega aao outro lado do tabuleiro
                    
        }
        
         // Remove a peça da posição anterior
        Button previousButton = chessboard[selectedRow][selectedCol];
        previousButton.setGraphic(null);

        boardState[selectedRow][selectedCol] = null;
        
        // Alterna a vez do jogador
        if("white".equals(playerColor)) {
            playerColor="black";
        } else {
            playerColor="white";
        }

    } else {
        // Movimento invalido
        resetSelection();
        return;
    }

    // Limpa a seleção depois da jogada
    resetSelection();
}
   
    private static void sendMessageToServer(String serverIp, int serverPort, String message) {
        try {
            // Obtem o output stream do socket
             OutputStream outputStream = socket.getOutputStream();

            // Converte a mensagem para bytes e escreve para o output stream
             outputStream.write(message.getBytes());
            } catch (IOException e) {
            // Lidar com qualquer erros IO
        }
    }
}