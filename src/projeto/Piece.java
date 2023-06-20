package projeto;

public class Piece {
    private String type; // Tipo da peça
    private String color; // Cor da peça 

    public Piece(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
    
    public void setColor(String Color) {
        color = Color;
    }
    
    public void setType(String Type) {
        type = Type;
    }
}
