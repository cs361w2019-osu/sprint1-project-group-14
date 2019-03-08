var isSetup = true;
var isPlayerTurn = true;
var gameOver = 0;
var placedShips = 0;
var game;
var shipType;
var vertical;
var currentWeapon = "BOMB";
var statusBar = document.getElementsByClassName("status-bar")[0];

var playerShipsMap = {
    "MINESWEEPER": "place_minesweeper",
    "DESTROYER": "place_destroyer",
    "BATTLESHIP": "place_battleship",
    "SUBMARINE": "place_submarine",
};
var opponentShipsMap = {
    "MINESWEEPER": "opponent_minesweeper",
    "DESTROYER": "opponent_destroyer",
    "BATTLESHIP": "opponent_battleship",
    "SUBMARINE": "opponent_submarine",
};

function makeGrid(table) {
    for (i = 0; i < 10; i++) {
        let row = document.createElement('tr');
        for (j = 0; j < 10; j++) {
            let column = document.createElement('td');
            column.addEventListener("click", cellClick);
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function markHits(board, elementId, surrenderNum) {
    var surrender = 0;
    board.attacks.forEach((attack) => {
        let className;
        if (attack.result === "MISS")
            className = "miss";
        else if (attack.result === "HIT")
            className = "hit";
        else if (attack.result === "SONAR") {
            className = "sonar";
            sonarPulse(board, attack.location.column, attack.location.row, elementId);
        }
        else if (attack.result === "SUNK")
            className = "sunk";
        else if (attack.result === "SURRENDER") {
            className = "sunk";
            surrender = surrenderNum;
        }

        if (className !== "sonar") {
            document.getElementById(elementId).rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
        }
        markActionBar(elementId, className);
    });
    return surrender;
}

function sonarPulse(board, col, row, target) {
    var lcol = col.charCodeAt(0) - 'A'.charCodeAt(0)

    board.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        var rcol = square.column.charCodeAt(0) - 'A'.charCodeAt(0)
        if ((row <= square.row + 2 && row >= square.row - 2) &&
            ((lcol >= rcol - 2) && (lcol <= rcol + 2)) && (Math.abs(lcol - rcol) + Math.abs(row - square.row) <= 2)) {
            document.getElementById(target).rows[square.row - 1].cells[rcol].classList.add("occupied");
        }
    }));

    for (i = row - 2; i <= row + 2; i++) {
        for (j = lcol - 2; j <= lcol + 2; j++) {
            if (i <= 10 && i >= 1 && j < 10 && j >= 0 && Math.abs(row - i) + Math.abs(lcol - j) <= 2 &&
                !document.getElementById(target).rows[i - 1].cells[j].classList.contains("occupied")) {
                document.getElementById(target).rows[i - 1].cells[j].classList.add("sonar");
            }
        }
    }
}

function checkWeapon(board) {
    if (board.currentWeapon != currentWeapon) {
        currentWeapon = board.currentWeapon;
        alert("Your weapon has been upgraded!");
    }
}

var getCellIndex = function (e) {
    if (document.getElementById("sonar_pulse").dataset.toggled === "false")
        return;

    var hoverCellIndex = e.srcElement.cellIndex;
    var hoverRowIndex = e.target.parentNode.rowIndex+1;

    for (i = hoverRowIndex - 2; i <= hoverRowIndex + 2; i++) {
        for (j = hoverCellIndex - 2; j <= hoverCellIndex + 2; j++) {
            if (i <= 10 && i >= 1 && j < 10 && j >= 0 && Math.abs(hoverRowIndex - i) + Math.abs(hoverCellIndex - j) <= 2) {
                document.getElementById('opponent').rows[i - 1].cells[j].style.background = "gray";
            }
        }
    }
}

var cleanCell = function (e) {
    var outCellIndex = e.srcElement.cellIndex;
    var outRowIndex = e.target.parentNode.rowIndex+1;

    for (i = outRowIndex - 2; i <= outRowIndex + 2; i++) {
        for (j = outCellIndex - 2; j <= outCellIndex + 2; j++) {
            if (i <= 10 && i >= 1 && j < 10 && j >= 0 && Math.abs(outRowIndex - i) + Math.abs(outCellIndex - j) <= 2) {
                document.getElementById('opponent').rows[i - 1].cells[j].style.background = "";
            }
        }
    }
}

function markActionBar(person, result) {
    personUpper = person.replace(/\b\w/g, function (l) { return l.toUpperCase() })

    if (result === "miss") {
        resultStatus = personUpper + " missed.";
    } else if (result == "hit") {
        resultStatus = personUpper + " was hit.";
    } else if (result == "sunk") {
        resultStatus = personUpper + "'s ship sunk.";
    } else if (result == "sonar") {
        resultStatus = personUpper + " was hit by sonar."
    } else {
        resultStatus = "-";
    };

    document.getElementsByClassName(person + "-result")[0].dataset.result = result;
    document.getElementsByClassName(person + "-result")[0].innerHTML = resultStatus;
}

function disableGrid(grid) {
    for (i = 0; i < 10; i++) {
        for (j = 0; j < 10; j++) {
            //remove the condition to make sure all the cell is disabled.
            grid.rows[i].cells[j].classList.add("disabled");
        }
    }
}

function enableGrid(grid) {
    for (i = 0; i < 10; i++) {
        for (j = 0; j < 10; j++) {
            if (grid.rows[i].cells[j].classList.contains("disabled")) {
                grid.rows[i].cells[j].classList.remove("disabled");
            }
        }
    }
}

function redrawGrid(person) {
    for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
            document.getElementById('opponent').rows[i].cells[j].removeEventListener("mouseover", getCellIndex);
            document.getElementById('opponent').rows[i].cells[j].removeEventListener("mouseout", cleanCell);
        }
    }
    Array.from(document.getElementById(person).childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById(person));

    if (game === undefined) {
        return;
    }
    if (person === "player") {
        game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
            document.getElementById("player").rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
        }));
        gameOver = markHits(game.playersBoard, person, 1);
    } else {
        gameOver = markHits(game.opponentsBoard, person, 2);
    }

    game[person + 'sBoard']['sunkShips'].forEach(function (s) {
        var e;
        if (person == "player") {
            e = playerShipsMap[s.shipName];
        } else {
            e = opponentShipsMap[s.shipName];
        }
        document.getElementById(e).dataset.sunk = "true";
    })
    for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
            document.getElementById('opponent').rows[i].cells[j].addEventListener("mouseover", getCellIndex);
            document.getElementById('opponent').rows[i].cells[j].addEventListener("mouseout", cleanCell);
        }
    }
    checkWeapon(game.opponentsBoard);
}

var oldListener;
function registerCellListener(f) {
    let el = document.getElementById("player");
    for (i = 0; i < 10; i++) {
        for (j = 0; j < 10; j++) {
            let cell = el.rows[i].cells[j];
            cell.removeEventListener("mouseover", oldListener);
            cell.removeEventListener("mouseout", oldListener);
            cell.addEventListener("mouseover", f);
            cell.addEventListener("mouseout", f);
        }
    }
    oldListener = f;
}

function playerWins() {
    redrawGrid("opponent");
    disableGrid(document.getElementById("opponent"));
    disableGrid(document.getElementById("player"))
    statusBar.innerText = "Player Wins";
    statusBar.dataset.winner = 'true';
}

function playerLost() {
    redrawGrid("player");
    disableGrid(document.getElementById("player"));
    disableGrid(document.getElementById("opponent"));
    statusBar.innerText = "Opponent Wins";
    statusBar.dataset.loser = 'true';
}

function endPlayerTurn() {
    redrawGrid("opponent");
    isPlayerTurn = false
    statusBar.innerText = "Opponent's turn to attack.";
    disableGrid(document.getElementById("opponent"));
    enableGrid(document.getElementById("player"))
}

function endOpponentTurn() {
    redrawGrid("player");
    isPlayerTurn = true
    statusBar.innerText = "Attack your opponent.";
    disableGrid(document.getElementById("player"))
    enableGrid(document.getElementById("opponent"))
}

function cellClick() {
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    if (isSetup) {
        sendXhr("POST", "/place", { game: game, shipType: shipType, x: row, y: col, isVertical: vertical }, function (data) {
            game = data;
            placedShips++;
            redrawGrid("player");
            redrawGrid("opponent");
            if (placedShips == 4) {
                isSetup = false;
                registerCellListener((e) => { });
            }
            document.getElementById(playerShipsMap[shipType]).dataset.placed = "true";

            disableGrid(document.getElementById("player"));
            isPlayerTurn = false;

            if (!isSetup) {
                disableGrid(document.getElementById("opponent"))
            } else {
                enableGrid(document.getElementById("opponent"));
            }

            statusBar.innerText = "Opponent placing ship.";
            window.setTimeout(function () {
                document.getElementById(opponentShipsMap[shipType]).dataset.placed = "true";
                document.getElementById(opponentShipsMap[shipType]).classList.remove("disabled");
                if (isSetup) {
                    disableGrid(document.getElementById("opponent"));
                    enableGrid(document.getElementById("player"));
                    statusBar.innerText = "Place your ship.";
                } else {
                    statusBar.innerText = "Attack your opponent.";
                    enableGrid(document.getElementById("opponent"))
                }
                isPlayerTurn = true;
            }, 1000);
        });
    } else if (isPlayerTurn && document.getElementById("sonar_pulse").dataset.toggled === "true") {
        sendXhr("POST", "/sonar", { game: game, x: row, y: col }, function (data) {
            game = data
            endPlayerTurn()
            window.setTimeout(function () {
                endOpponentTurn()
            }, 1000);
        });
        document.getElementById("sonar_pulse").dataset.toggled = "false";
        document.getElementById("sonar_pulse").innerHTML = "<i class=\"fas fa-broadcast-tower\"></i>&nbsp;Sonar: Off";
    } else if (isPlayerTurn) {
        sendXhr("POST", "/attack", { game: game, x: row, y: col }, function (data) {
            game = data;
            endPlayerTurn()
            if (gameOver === 0) {
                window.setTimeout(function () {
                    endOpponentTurn()
                    if (gameOver === 1) {
                        playerLost()
                    }
                }, 1000);
            } else if (gameOver === 2) {
                playerWins()
            }
        })
    }
}

function blinkInvalid() {
    statusBar.dataset.invalid = "true";
    let oldText = statusBar.innerText;
    statusBar.innerText = "Invalid Operation";
    window.setTimeout(function () {
        statusBar.innerText = oldText;
        statusBar.dataset.invalid = "false";
    }, 500);
}

function redBlink(grid) {
    grid.style.border = "2px solid red";
    markActionBar(grid.getAttribute("id"), "-");
    window.setTimeout(function () {
        grid.style.border = "";
    }, 200);
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function (event) {
        if (req.status != 200) {
            redBlink(document.getElementById('player'));
            redBlink(document.getElementById('opponent'));
            blinkInvalid();
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function place(size) {
    return function () {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = (document.getElementById("is_vertical").dataset.toggled) == "true";
        let table = document.getElementById("player");
        for (let i = 0; i < size; i++) {
            let cell;
            if (vertical) {
                let tableRow = table.rows[row + i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col + i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                break;
            }
            cell.classList.toggle("placed");
        }
    }
}

// More customizable highlighting. Takes in template position 0-indexed
// length of highlight for vertical rotation.
function place2D(pos, len) {
    return function () {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = (document.getElementById("is_vertical").dataset.toggled) == "true";
        let table = document.getElementById("player");

        pos.forEach(function(cord) {
            let row_off = cord[0];
            let col_off = cord[1];
            let cell;
            if (vertical) {
                let temp = row_off;
                row_off = col_off;
                col_off = len - temp - 3;
            }

            let r = table.rows[row+row_off]

            if (r !== undefined && r.cells[col+col_off] !== undefined) {
                cell = r.cells[col+col_off]
                cell.classList.toggle("placed");
            }
        });
    }
}

function registerMove(dirs) {
    dirs.forEach(function(dir) {
        document.getElementById("move_fleet_" + dir).addEventListener("click", function (e) {
            sendXhr("POST", "/move", { game: game, dir:dir.toUpperCase()}, function (data) {
                game = data;
                redrawGrid('player');
            });
        });
    });
}

function initGame() {
    let p_ships = ["place_minesweeper", "place_destroyer", "place_battleship", "place_submarine"];
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    statusBar.innerText = "Place your ship.";
    document.getElementById("place_minesweeper").addEventListener("click", function (e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "MINESWEEPER";
        let s = document.getElementById("place_minesweeper");
        p_ships.forEach(function (ship) {
            let s = document.getElementById(ship);
            if (s.dataset.placed == "false") {
                s.dataset.selected = "false";
            };
        });

        s.dataset.selected = true;


        if (s.dataset.placed == "false") {
            registerCellListener(place(2));
        }
    });
    document.getElementById("place_destroyer").addEventListener("click", function (e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "DESTROYER";
        let s = document.getElementById("place_destroyer");
        p_ships.forEach(function (ship) {
            let s = document.getElementById(ship);
            if (s.dataset.placed == "false") {
                s.dataset.selected = "false";
            };
        });
        s.dataset.selected = "true";

        if (s.dataset.placed == "false" && isPlayerTurn) {
            registerCellListener(place(3));
        }
    });
    document.getElementById("place_battleship").addEventListener("click", function (e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "BATTLESHIP";
        let s = document.getElementById("place_battleship");
        p_ships.forEach(function (ship) {
            let s = document.getElementById(ship);
            if (s.dataset.placed == "false") {
                s.dataset.selected = "false";
            };
        });
        s.dataset.selected = "true";

        if (s.dataset.placed == "false" && isPlayerTurn) {
            registerCellListener(place(4));
        }
    });

    document.getElementById("place_submarine").addEventListener("click", function (e) {
        if (!isPlayerTurn) {
            return
        }
        shipType = "SUBMARINE";
        let s = document.getElementById("place_submarine");
        p_ships.forEach(function (ship) {
            let s = document.getElementById(ship);
            if (s.dataset.placed == "false") {
                s.dataset.selected = "false";
            };
        });
        s.dataset.selected = "true";

        if (s.dataset.placed == "false" && isPlayerTurn) {
            registerCellListener(place2D([[1,0], [1,1], [1,2], [1,3], [0,2]], 4));
        }
    });

    document.getElementById("is_vertical").addEventListener("click", function (e) {
        let b = e.srcElement;
        if (b.dataset.toggled == "true") {
            b.innerHTML = "Mode: Horizontal"
            b.dataset.toggled = "false";
        } else {
            b.innerHTML = "Mode: Vertical"
            b.dataset.toggled = "true";
        };
    });
    document.getElementById("sonar_pulse").addEventListener("click", function (e) {
        let b = e.srcElement;
        if (b.dataset.toggled == "true") {
            b.innerHTML = "<i class=\"fas fa-broadcast-tower\"></i>&nbsp;Sonar: Off"
            b.dataset.toggled = "false";
        } else {
            b.innerHTML = "<i class=\"fas fa-broadcast-tower\"></i>&nbsp;Sonar: On"
            b.dataset.toggled = "true";
        };
    });

    registerMove(['north','south','east','west'])

    document.getElementById("reset").addEventListener("click", function (e) {
        window.location.reload(false);
    });
    disableGrid(document.getElementById("opponent"));
    sendXhr("GET", "/game", {}, function (data) {
        game = data;
    });
};