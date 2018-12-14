package packet;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        System.out.println("Maze v1.4 upd: Поддержка нескольких комнат");
        System.out.println("__________________________________________\n");

        int[][] maze = generateMaze(21, false);
        for(int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze.length; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static int[][] generateMaze(int size, boolean multiPath){

        // Гайд по цифрам:
        // 0-стена
        // 1-обычный проход
        // 2-выход из комнаты(антибаг)
        // 3-стены комнаты

        int roomsCount = 3; // Выставляем количство комнат

        Random random = new Random();
        //int maze[][] = new int[size][size];                                                                                   // Пустой квадрат
        int maze[][] = createDungeon(size, roomsCount, true);
        maze[0][0] = 1;                                                                                                         // Задаем стартовую позицию как посещенную
        int keyCellsCount = (size % 2 != 0) ? ((int)Math.pow((size + 1), 2) / 4) : ((int)Math.pow((size), 2) / 4);              // Колличество ключевых ячеек, где будем останавливаться
        int filledKeyCells = 1 + (roomsCount * 4); //TODO: сделать динамическое получение заполеных клеток от комнат(класс)     // Заполненные ключевые ячейки
        int nowPos = 0;                                                                                                         // Текущая позиция
        Coordinates[] position = new Coordinates[keyCellsCount];                                                                // Массив с пройденными координатами
        position[nowPos] = new Coordinates();
        position[nowPos].setX(0);
        position[nowPos].setY(0);

        while(filledKeyCells < keyCellsCount){
            if(checkCellsAround(maze, position[nowPos])){
                int rndValue = random.nextInt(4) + 1;
                switch(rndValue){
                    case 1: { // x- вверх
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX() - 1][position[nowPos].getY()] = 1;
                        maze[position[nowPos].getX() - 2][position[nowPos].getY()] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX() - 2);
                        position[nowPos].setY(position[nowPos - 1].getY());
                        filledKeyCells++;
                        break;
                    }
                    case 2: { // x+ вниз
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX() + 1][position[nowPos].getY()] = 1;
                        maze[position[nowPos].getX() + 2][position[nowPos].getY()] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX() + 2);
                        position[nowPos].setY(position[nowPos - 1].getY());
                        filledKeyCells++;
                        break;
                    }
                    case 3: { // y- влево
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX()][position[nowPos].getY() - 1] = 1;
                        maze[position[nowPos].getX()][position[nowPos].getY() - 2] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX());
                        position[nowPos].setY(position[nowPos - 1].getY() - 2);
                        filledKeyCells++;
                        break;
                    }
                    case 4: { // y+ вправо
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX()][position[nowPos].getY() + 1] = 1;
                        maze[position[nowPos].getX()][position[nowPos].getY() + 2] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX());
                        position[nowPos].setY(position[nowPos - 1].getY() + 2);
                        filledKeyCells++;
                        break;
                    }
                }
            }
            else{
                nowPos--;
            }
        }

        if(multiPath){                                                      // Ломаем стены для вариативности прохождения

            int breakingWallsCount = (int)Math.pow((size / 5), 2);          // Колличество ломаемых стен

            for(int i = 0; i <  breakingWallsCount; i++) {
                boolean exit = false;
                do {
                    int randomX = random.nextInt(size - 1);
                    int randomY = random.nextInt(size - 1);
                    int way = random.nextInt(4) - 1;
                    if((randomX % 2 == 0 && randomY % 2 != 0) || (randomX % 2 != 0 && randomY % 2 == 0)) {
                        if(checkCellGoTo(maze, new Coordinates(randomX, randomY), way, 1)) {
                            System.out.println("X:" + randomX + " Y:" + randomY);
                            maze[randomX][randomY] = 1;
                            exit = true;
                        }
                    }
                } while(!exit);
            }


        }

        return maze;
    }

    private static int[][] createDungeon(int size, int roomsCount, boolean isDungeon) {

        int[][] dungeon = new int[size][size];

        if(isDungeon) { // TODO:сделать проверку на влезаемость комнат
            for (int i = 0; i < roomsCount; i++) {
                createRoomInDungeon(dungeon, 3, 3);
            }
        }

        return dungeon;
    }

    private static int[][] createRoomInDungeon(int[][] dungeon, int width, int height) { // width и height задаются без учета стен

        int maxRandomXValue = (dungeon.length - 2) - width - 2; // Задаем максимальные значения рандома координат
        int maxRandomYValue = (dungeon.length - 2) - height - 2;

        if (maxRandomXValue < 0 || maxRandomYValue < 0) { // Если не подходит по параметрам возвращаем лабиринт без комнаты
            System.out.println("Maze too small");
            return dungeon;
        }

        Random random = new Random(); // Генерируем координаты
        int randomX, randomY;

            // Проверка на возможность добавления комнаты
        boolean exit;
        int failCounter = 0; // Количество неудачных попыток создания начальной точки для комнаты, до выхода из генерации
        do {
            exit = true;
            randomX = maxRandomXValue > 0 ? random.nextInt(maxRandomXValue) + 2 : 2;
            randomY = maxRandomYValue > 0 ? random.nextInt(maxRandomYValue) + 2 : 2;
            if(randomX % 2 != 0 ) randomX++;
            if(randomY % 2 != 0 ) randomY++;

            for(int i = 0; i < width+2; i++){
                for(int j = 0; j < height+2; j++){
                    if(dungeon[randomX+i][randomY+j] != 0) {
                        failCounter++;
                        exit = false;
                    }
                }
            }

            if(failCounter > 10) {                               // Регулируем количество попыток создания начальной точки
                System.out.println("Not enough place");
                return dungeon;
            }
        } while(!exit);                                                         // TODO:[SOLVED]возможен баг, если вторая комната больше первой, пока такого функционала нет[SOLVED]

        for(int i = 0; i < width; i++) { // Генерируем комнату
            for (int j = 0; j < height; j++) {
                dungeon[randomX + i][randomY + j] = 1;

                if(i == 0) dungeon[randomX + i - 1][randomY + j] = 3;       // Делаем стены для комнаты (опционально, можно выпилить, использовать можно
                if(i == 0) dungeon[randomX + i + width][randomY + j] = 3;   // как для изменения цвета стен, так и для того, чтобы не рандомило разбитие)
                if(j == 0) dungeon[randomX + i][randomY + j - 1] = 3;
                if(j == 0) dungeon[randomX + i][randomY + j + height] = 3;
            }
        }
        // TODO:[SOLVED]баг, комнаты могут встать полностью вертикально или горизонтально и выходы срандомятся друг в друга[SOLVED]
        dungeon[randomX - 1][randomY - 1] = 3;
        dungeon[randomX + width][randomY + height] = 3;
        dungeon[randomX + width][randomY - 1] = 3;
        dungeon[randomX - 1][randomY + height] = 3;

        // TODO:[SOLVED] Исправить баг, когда выход из комнаты, ведет вникуда(можно присваивать выходу не единицу а 2...[SOLVED]
        int way = random.nextInt(4) + 1; // Делаем вход в комнату
        if(way == 1) { dungeon[randomX + (width/2)][randomY - 1] = 1; dungeon[randomX + (width/2)][randomY - 2] = 2; }
        if(way == 2) { dungeon[randomX + (width/2)][randomY + height] = 1; dungeon[randomX + (width/2)][randomY + height + 1] = 2; }
        if(way == 3) { dungeon[randomX - 1][randomY + (height/2)] = 1; dungeon[randomX - 2][randomY + (height/2)] = 2; }
        if(way == 4) { dungeon[randomX + width][randomY + (height/2)] = 1; dungeon[randomX + width + 1][randomY + (height/2)] = 2; }

        return dungeon;
    }

    private static boolean checkCellsAround(int[][] maze, Coordinates position) {

        if(position.getX() - 2 >= 0) if(maze[position.getX() - 2][position.getY()] != 1) return true;            // x-
        if(position.getX() + 2 <= maze.length) if(maze[position.getX() + 2][position.getY()] != 1) return true;  // x+
        if(position.getY() - 2 >= 0) if(maze[position.getX()][position.getY() - 2] != 1) return true;            // y-
        if(position.getY() + 2 <= maze.length) if(maze[position.getX()][position.getY() + 2] != 1) return true;  // y+

        return false;
    }

    private static boolean checkCellGoTo(int[][] maze, Coordinates position, int way, int keyOrWall) {  // way - направление, keyOrWall - 2-ключевая клетка, 1-стена

        if(way == 1) if(position.getX() - 2 >= 0) if(maze[position.getX() - keyOrWall][position.getY()] != 1) return true;            // x-
        if(way == 2) if(position.getX() + 2 <= maze.length) if(maze[position.getX() + keyOrWall][position.getY()] != 1) return true;  // x+
        if(way == 3) if(position.getY() - 2 >= 0) if(maze[position.getX()][position.getY() - keyOrWall] != 1) return true;            // y-
        if(way == 4) if(position.getY() + 2 <= maze.length) if(maze[position.getX()][position.getY() + keyOrWall] != 1) return true;  // y+

        return false;
    }

    private static class Coordinates {

        private int x;
        private int y;

        public Coordinates() {

        }

        public Coordinates(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
