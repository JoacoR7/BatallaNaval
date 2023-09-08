package battleShip.entities;

import battleShip.entities.Ships.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static java.lang.Math.abs;
/**
 * Una clase para representar al jugador
 * Cada jugador es creado junto con un nombre, un mapa, una lista de barcos asociados y un numero de cargas
 * @version 1.4, 6/9/2023
 * @author Martin Farrés
 */
public class Player {
    private final String name;
    private final ArrayList<Ship> ships;
    private final Map map;
    private int charges;

    /**
     * Construye un jugador a partir de su nombre
     *
     * @param name El nombre del jugador.
     */
    public Player(String name) {
        this.name = name;
        this.ships = new ArrayList<>();
        this.ships.add(new Boat());
        this.ships.add(new Cruiser());
        this.ships.add(new Submarine());
        this.ships.add(new Warship());
        this.ships.add(new AircraftCarrier());
        this.map = new Map();
        this.charges = 0;
    }

    //Getters
    public String getName() {
        return name;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public Map getMap() {
        return map;
    }

    public int getCharges() {
        return charges;
    }

    //Setters
    public void setCharges(int charges) {
        this.charges = charges;
    }

    //Methods

    /**
     * Checkea que sea posible colocar el barco y de ser posible lo asocia en las celdas indicadas
     * Ejemplo de input: ("A5", "A7", Submarine)
     *
     * @param startPos Posicion inicial donde se coloca la cabecilla del barco.
     * @param finalPos Posicion final donde termina el barco
     * @param ship     Tipo de barco colocado
     * @return true si la posicion es valida y se coloco exitosamente, o false si la posicion
     * no es valida y levanta una excepcion
     */
    public boolean placeShip(@NotNull String startPos, @NotNull String finalPos, @NotNull Ship ship) {
        int row = startPos.substring(0, 1).charAt(0) - 'A';
        int col = Integer.parseInt(startPos.substring(1)) - 1;
        int rowFinal = finalPos.substring(0, 1).charAt(0) - 'A';
        int colFinal = Integer.parseInt(finalPos.substring(1)) - 1;
        ArrayList<Cell> validCells = new ArrayList<>();

        try {
            // Check it is not out of bounds
            if ((this.map.getCell(startPos) == null) || (this.map.getCell(finalPos) == null)) {
                throw new Exception("The position is out of bounds");
            }

            // Check it is not diagonal
            if (abs(row - rowFinal) > 0 && abs(col - colFinal) > 0) {
                throw new Exception("The Position can't be diagonal");
            }

            // Checks length of boat is the same as length of placement
            if ((abs(row - rowFinal) + abs(col - colFinal)) != ship.length - 1) {
                throw new Exception("The position selected should support the ships length: " + ship.length);
            }

            // Are all the position selected available?
            if (abs(row - rowFinal) > 0) { // Movement in should be in row or col?
                for (int j = row; j <= rowFinal; j++) { // Checks all positions are available, if not raises exception
                    helperPlaceShip(j, col, validCells);
                }
            } else if (abs(col - colFinal) > 0) {
                for (int j = col; j <= colFinal; j++) {
                    helperPlaceShip(row, j, validCells);
                }
            } else {
                helperPlaceShip(row, col, validCells);
            }

            //Place ship inside cell
            for (Cell cell : validCells) {
                cell.setElement(ship);
                ship.setOccupiedCells(cell);
            }

        } catch (Exception e) {
            System.out.printf("%s", e);
            return false;
        }

        return true;
    }

    // Example Input: (2, 2, {Cell (2,3), Cell (4,5),...})
    // Checks up, down, right and left in every cell for ships, and if all cells are empty the cell is added to the ArrayList
    private void helperPlaceShip(int row, int col, ArrayList<Cell> validCells) throws Exception { //Se puede mejorar pq checkea mas de una vez a ciertas celdas
        for (int i = -1; i < 2; i++) { //Checks one position to its right,left,up,down
            Cell cellXMov = this.map.getCell(row + i, col);
            Cell cellYMov = this.map.getCell(row, col + i);

            if (i != 0) { // if pos checked is out of bound, ignored it
                if (cellXMov != null) {
                    if (cellXMov.getElement() != null) { // if ship in pos checked, raise exception
                        throw new Exception("The position is not valid");
                    }
                } else if (cellYMov != null) {
                    if (cellYMov.getElement() != null) {
                        throw new Exception("The position is not valid");
                    }
                }
            }

        }
        validCells.add(this.map.getCell(row, col));

    }

    // Input Example: ("B9", Player() p2)
    // Returns true if the shot can be shot, sets the element to the p1 map and calls the methods Cell.shot()
    // Returns false if shot cant be done and raises exception
    public Boolean shoot(@NotNull String pos, @NotNull Player p2) { // String like "A6" expected
        Cell cellP1 = this.map.getCell(pos);
        Cell cellP2 = p2.getMap().getCell(pos);
        try {
            cellP2.shot(); //shot p2
            this.map.addShotCell(cellP1);
            return true;
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    public Boolean shootAbility(Player player, String pos, Player Enemy, Ship ship){
        if (ship.useCharges(player)) {
            ship.useAbility(player, pos, Enemy);
        }else{
            System.out.println("Not enough charges");
            return false;
        }

        return true;
    }
}

