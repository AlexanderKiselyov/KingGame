# Серверная часть игры "Кинг"

## Контракт взаимодействия клиентов и сервера

1. Клиент подключается к серверу во время запуска приложения. Он инициализирует подключение по URI `ws://localhost:8080/ws/`

Сервер создает новую сессию и отправляет клиенту данные:

```json
{
    "session_id" : <id сессии типа string>
}
```

Клиент сохраняет у себя номер сессии для дальнейшего обмена информацией.

2. Во время начала игры клиент отправляет серверу данные:

```json
{
    "session_id" : <id сессии>,
    "player_name" : <имя игрока типа string>,
    "action" : "play"
}
```

На сервере клиент добавляется в очередь желающих зайти в игру и ждет. Как только в очереди оказывается 4 желающих, формируется
игровая сессия, для каждого игрока создается числовой `id` и всем 4 клиентам отправляются следующие данные от сервера:

```json
{
    "game_session_id" : <id новой игровой сессии типа integer>,
    "game_state" : {
        "state" : "started",
        "started_by" : <id игрока, начавшего или возобновившего игру>,
        "game_num" : 1,
        "circle_num" : 1,
        "player_turn" : <id игрока, который должен ходить типа integer>,
        "players" : [
            {
                "player_id" : <id игрока 1 типа integer>
                "player_name" : <имя игрока 1>
                "points" : 0
            },
            {
                "player_id" : <id игрока 2 типа integer>
                "player_name" : <имя игрока 2>
                "points" : 0
            },
            {
                "player_id" : <id игрока 3 типа integer>
                "player_name" : <имя игрока 3>
                "points" : 0
            },
            {
                "player_id" : <id игрока 4 типа integer>
                "player_name" : <имя игрока 4>
                "points" : 0
            }
        ],
        "cards" : [
            {
                "suit" : <масть карты типа string из "hearts", "clubs", "diamonds", "spades">
                "magnitude" : <достоинство карты типа integer из диапазона 7-14 (11 - валет, 12 - дама, 13 - король, 14 - туз)>
            },
            ... <всего 8 карт>
            {
                "suit" : <масть карты типа string из "hearts", "clubs", "diamonds", "spades">
                "magnitude" : <достоинство карты типа integer из диапазона 7-14 (11 - валет, 12 - дама, 13 - король, 14 - туз)>
            }
        ],
        "bribe" : []
    }
}
```

Здесь:

* `game_state` - текущее состояние игры (объект, у которого поле `state` типа `string` из набора:
  * `started` (во время игры),
  * `paused` (если хотя бы один игрок поставил игру на паузу или случились проблемы с соединением (соединение оказалось закрытым)),
  * `cancelled` (если по истечении 180 секунд после входа в состояние `paused`, игровая сессия не вышла из состояния paused),
  * `finished` (после хода игрока на последнем круге последней игры).

* `started_by` - `id` игрока, который начал или возобновил игру

* `game_num` - номер текущей игры (тип `integer` из диапазона 1-12)

* `circle_num` - номер круга в текущей игре (типа `integer` из диапазона 1-8)

* `players` - список игроков (первый игрок - данный игрок, которому отправляются данные, далее - по часовой стрелке от данного игрока)

* `cards` - текущий набор карт для данного игрока

* `bribe` - взятка на текущем круге (карты, выложенные игроками на текущем круге)

Клиенту необходимо сохранить свой `id`.

Право первого хода в каждой новой текущей игре дается случайным образом, далее в рамках этой текущей игры начинают каждый новый круг
по кругу, то есть первый круг начинает случайный игрок, второй круг - игрок, сидящий по часовой стрелке от него и т. д.

3. Во время игры клиент после своего хода посылает следующие данные по своему ходу:

```json
{
    "game_session_id" : <id игровой сессии>,
    "action" : "turn",
    "game_state" : {
        "game_num" : <номер текущей игры>,
        "circle_num" : <номер круга>
    },
    "player_id" : <номер игрока>,
    "turn" : {
        "suit" : <масть карты>,
        "magnitude" : <достоинство карты>
    }
}
```

Во время игры клиенту приходят (после хода каждого из игроков в игровой сессии) от сервера следующие данные:

```json
{
    "game_session_id" : <id игровой сессии>,
    "game_state" : {
        "state" : "started",
        "started_by" : <id игрока, начавшего или возобновившего игру>,
        "game_num" : <номер текущей игры>,
        "circle_num" : <номер круга>,
        "player_turn" : <id игрока, который должен ходить>,
        "players" : [
            {
                "player_id" : <id игрока 1>
                "player_name" : <имя игрока 1>
                "points" : <очки игрока 1>
            },
            {
                "player_id" : <id игрока 2>
                "player_name" : <имя игрока 2>
                "points" : <очки игрока 2>
            },
            {
                "player_id" : <id игрока 3>
                "player_name" : <имя игрока 3>
                "points" : <очки игрока 3>
            },
            {
                "player_id" : <id игрока 4>
                "player_name" : <имя игрока 4>
                "points" : <очки игрока 4>
            }
        ],
        "cards" : [
            {
                "suit" : <масть карты>
                "magnitude" : <достоинство карты>
            },
            ... <количество карт зависит от номера круга>
            {
                "suit" : <масть карты>
                "magnitude" : <достоинство карты>
            }
        ],
        "bribe" : [
            {
                "suit" : <масть карты>
                "magnitude" : <достоинство карты>
            },
            ... <количество карт зависит от количества игроков, сделавших ход>
            {
                "suit" : <масть карты>
                "magnitude" : <достоинство карты>
            }
        ]
    }
}
```

Клиент может ходить только тогда, когда в поле `player_turn` стоит `id`, совпадающий с клиентским `id`.

4. В случае постановки игроком игры на паузу, происходит отправка клиентом серверу данных:

```json
{
    "game_session_id" : <id игровой сессии>
    "player_id" : <номер игрока>,
    "action" : "pause"
}
```

В таком случае, а также в случае невозможности отправки хотя бы одному сообщений по веб-сокет соединению (состояние
соединения - `closed`), сервер отправляет остальным игрокам следующие данные:

```json
{
    "game_session_id" : <id игровой сессии>,
    "game_state" : {
        "state" : "paused",
        "paused_by" : <id игрока, остановившего игру>,
        "game_num" : <номер текущей игры>,
        "circle_num" : <номер круга>,
        "players" : [
            {
                "player_id" : <id игрока 1>
                "player_name" : <имя игрока 1>
                "points" : <очки игрока 1>
            },
            {
            "player_id" : <id игрока 2>
                "player_name" : <имя игрока 2>
                "points" : <очки игрока 2>
            },
            {
                "player_id" : <id игрока 3>
                "player_name" : <имя игрока 3>
                "points" : <очки игрока 3>
            },
            {
                "player_id" : <id игрока 4>
                "player_name" : <имя игрока 4>
                "points" : <очки игрока 4>
            }
        ]
    }
}
```

После получения данного сообщения клиент сообщает пользователю, что игра находится на паузе по причине постановки на паузу
другим игроком или отсутствии соединения с другими игроками и ждет, пока от сервера не придет сообщение с `game_state.state = started`.

После выхода игрока из паузы, он отправляет серверу сообщение:

```json
{
    "game_session_id" : <id игровой сессии>
    "player_id" : <номер игрока>,
    "action" : "resume"
}
```

После получения данного сообщения сервер начинает отправлять игрокам текущие данные по состояние игры с `game_state.state = started`.

В случае, если клиент во время игры потерял соединение с сервером (состояние соединения - `closed`), он пытается переподключиться
и отправляет данные:

```json
{
    "game_session_id" : <id игровой сессии>
    "player_id" : <номер игрока>,
    "action" : "reconnect"
}
```

Далее клиент ждет от сервера сообщение с `game_state.state = started`, тем временем на сервере происходит перезапись `id` соединения у данного
пользователя в текущей игровой сессии и ответ всем клиентам в текущей игре о возобновлении игры.

Если за время 180 секунд игровая сессия не выйдет из состояния `paused`, то от сервера всем соединенным клиентам отправляется сообщение:

```json
{
    "game_session_id" : <id игровой сессии>,
    "game_state" : {
        "state" : "cancelled",
        "cancelled_by" : <id игрока, из-за которого игра не состоялась>,
        "game_num" : <номер текущей игры>,
        "circle_num" : <номер круга>,
        "players" : [
            {
                "player_id" : <id игрока 1>
                "player_name" : <имя игрока 1>
                "points" : <очки игрока 1>
            },
            {
            "player_id" : <id игрока 2>
                "player_name" : <имя игрока 2>
                "points" : <очки игрока 2>
            },
            {
                "player_id" : <id игрока 3>
                "player_name" : <имя игрока 3>
                "points" : <очки игрока 3>
            },
            {
                "player_id" : <id игрока 4>
                "player_name" : <имя игрока 4>
                "points" : <очки игрока 4>
            }
        ]
    }
}
```

В этом случае клиент сообщает пользователю, что игра не состоялась.

5. Когда от сервера приходит сообщение вида:

```json
{
    "game_session_id" : <id игровой сессии>,
    "game_state" : {
        "state" : "finished",
        "winner" : <id победителя>,
        "players" : [
            {
                "player_id" : <id игрока 1>
                "player_name" : <имя игрока 1>
                "points" : <очки игрока 1>
            },
            {
                "player_id" : <id игрока 2>
                "player_name" : <имя игрока 2>
                "points" : <очки игрока 2>
            },
            {
                "player_id" : <id игрока 3>
                "player_name" : <имя игрока 3>
                "points" : <очки игрока 3>
            },
            {
                "player_id" : <id игрока 4>
                "player_name" : <имя игрока 4>
                "points" : <очки игрока 4>
            }
        ]
    }
}
```

Клиент сообщает пользователю об окончании игры и текущих результатах.

6. В случае непредвиденных ошибок на стороне сервера во время игры, сервер отправляет игрокам сообщение:

```json
{
    "game_state" : {
        "state" : "cancelled"
    }
}
```

В этом случае клиент считает, что игра не состоялась и уведомляет об этом игрока.
