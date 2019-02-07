var isSetup = true;
var isPlayerTurn = true;
var placedShips = 0;
var game;
var shipType;
var vertical;

function makeGrid(table) {
    for (i=0; i<10; i++) {
        let row = document.createElement('tr');
        for (j=0; j<10; j++) {
            let column = document.createElement('td');
            column.addEventListener("click", cellClick);
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function markHits(board, elementId, surrenderText) {
    board.attacks.forEach((attack) => {
        let className;
        if (attack.result === "MISS")
            className = "miss";
        else if (attack.result === "HIT")
            className = "hit";
        else if (attack.result === "SUNK")
            className = "hit"
        else if (attack.result === "SURRENDER")
            alert(surrenderText);
        document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
    });
}

function disableGrid(grid) {
    for (i=0; i<10; i++) {
        for (j=0; j<10; j++) {
            if (grid.rows[i].cells[j].classList.length == 0) {
                grid.rows[i].cells[j].classList.add("disabled");
            }
        }
    }
}

function enableGrid(grid) {
    for (i=0; i<10; i++) {
        for (j=0; j<10; j++) {
            if (grid.rows[i].cells[j].classList.contains("disabled")) {
                grid.rows[i].cells[j].classList.remove("disabled");
            }
        }
    }
}

function redrawGrid(person) {
    Array.from(document.getElementById(person).childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById(person));

    if (game === undefined) {
        return;
    }
    if (person === "player") {
        game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
            document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
        }));
        markHits(game.playersBoard, person, "You lost the game");
    } else {
        markHits(game.opponentsBoard, person, "You won the game");
    }
}

var oldListener;
function registerCellListener(f) {
    let el = document.getElementById("player");
    for (i=0; i<10; i++) {
        for (j=0; j<10; j++) {
            let cell = el.rows[i].cells[j];
            cell.removeEventListener("mouseover", oldListener);
            cell.removeEventListener("mouseout", oldListener);
            cell.addEventListener("mouseover", f);
            cell.addEventListener("mouseout", f);
        }
    }
    oldListener = f;
}

function cellClick() {
    let playerShipsMap = {"MINESWEEPER" : "place_minesweeper",
                          "DESTROYER"   : "place_destroyer",
                          "BATTLESHIP"  : "place_battleship"};
    let opponentShipsMap = {"MINESWEEPER" : "opponent_minesweeper",
                            "DESTROYER"   : "opponent_destroyer",
                            "BATTLESHIP"  : "opponent_battleship"};
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    if (isSetup) {
        sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical}, function(data) {
            game = data;
            placedShips++;
            redrawGrid("player");
            redrawGrid("opponent");
            if (placedShips == 3) {
                isSetup = false;
                registerCellListener((e) => {});
            }
            document.getElementById(playerShipsMap[shipType]).dataset.placed = "true";

            disableGrid(document.getElementById("player"));
            enableGrid(document.getElementById("opponent"));
            isPlayerTurn = false;

            window.setTimeout(function() {
                document.getElementById(opponentShipsMap[shipType]).dataset.placed = "true";
                document.getElementById(opponentShipsMap[shipType]).classList.remove("disabled");
                if (isSetup) {
                    disableGrid(document.getElementById("opponent"));
                    enableGrid(document.getElementById("player"));
                }
                isPlayerTurn = true;
            }, 1000);
        });
    } else if (isPlayerTurn){
        sendXhr("POST", "/attack", {game: game, x: row, y: col}, function(data) {
            game = data;

            redrawGrid("opponent");
            isPlayerTurn = false
            disableGrid(document.getElementById("opponent"));
            enableGrid(document.getElementById("player"))
            window.setTimeout(function(){
                redrawGrid("player");
                isPlayerTurn = true
                disableGrid(document.getElementById("player"))
                enableGrid(document.getElementById("opponent"))
            }, 1000);
        })
    }
}

function redBlink(grid) {
    grid.style.border = "2px solid red";
    window.setTimeout(function(){
        grid.style.border = "1px solid black";
    }, 200);
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function(event) {
        if (req.status != 200) {
            redBlink(document.getElementById('player'));
            redBlink(document.getElementById('opponent'));
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = (document.getElementById("is_vertical").dataset.toggled) == "true";
        let table = document.getElementById("player");
        for (let i=0; i<size; i++) {
            let cell;
            if(vertical) {
                let tableRow = table.rows[row+i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col+i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                break;
            }
            cell.classList.toggle("placed");
        }
    }
}

function initGame() {
    let p_ships = ["place_minesweeper", "place_destroyer", "place_battleship"];
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    document.getElementById("place_minesweeper").addEventListener("click", function(e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "MINESWEEPER";
        let s = document.getElementById("place_minesweeper");
        p_ships.forEach(function(ship) {
            let s = document.getElementById(ship);
            if(s.dataset.placed == "false") {
                s.dataset.selected = "false";
            };
        });

        s.dataset.selected = true;


        if (s.dataset.placed == "false") {
            registerCellListener(place(2));
        }
    });
    document.getElementById("place_destroyer").addEventListener("click", function(e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "DESTROYER";
        let s = document.getElementById("place_destroyer");
        p_ships.forEach(function(ship) {
             let s = document.getElementById(ship);
             if(s.dataset.placed == "false") {
                 s.dataset.selected = "false";
             };
        });
        s.dataset.selected = "true";

        if (s.dataset.placed == "false" && isPlayerTurn) {
            registerCellListener(place(3));
        }
    });
    document.getElementById("place_battleship").addEventListener("click", function(e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "BATTLESHIP";
        let s = document.getElementById("place_battleship");
        p_ships.forEach(function(ship) {
             let s = document.getElementById(ship);
             if(s.dataset.placed == "false") {
                 s.dataset.selected = "false";
             };
        });
        s.dataset.selected = "true";

        if (s.dataset.placed == "false" && isPlayerTurn) {
            registerCellListener(place(4));
        }
    });
    document.getElementById("is_vertical").addEventListener("click", function(e) {
        let b = e.srcElement;
        if (b.dataset.toggled == "true") {
            b.innerHTML = "Mode: Horizontal"
            b.dataset.toggled = "false";
        } else {
            b.innerHTML = "Mode: Vertical"
            b.dataset.toggled = "true";
        };
    });
    disableGrid(document.getElementById("opponent"));
    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });
};
