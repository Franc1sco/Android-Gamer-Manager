package com.example.audiovisualmanager.database

import com.example.audiovisualmanager.model.User
import java.sql.*
import java.util.*
import com.example.audiovisualmanager.model.Game


class MysqlManager {
    companion object {
        val instance = MysqlManager()
        //Declaramos el nombre de la base de datos
        private const val DATABASE_NAME = "AudiovisualManagerDatabase"

        //Declaramos los nombre de las tablas
        private const val TABLE_USER = "UserTable"
        private const val TABLE_USERGAMES = "UserGames"
        private const val TABLE_GAMES = "Games"
        private const val TABLE_FOLLOWERS = "Followers"

        //Declaramos los valores de las columnas
        //TABLA UserTable
        private const val ID = "id"
        private const val NICK_USER = "nick"
        private const val PASSWORD_USER = "password"
        private const val PRIVATE_USER = "private"

        //TABLA UserGames intermediaria
        private const val GAMEID = "gameid"
        private const val GAMEVALIDATED = "validated"

        //TABLA GAMES
        private const val GAME_NAME = "name"
        private const val GAME_PLATFORM = "platform"
        private const val GAME_GENRE = "genre"
        private const val GAME_COMPANY = "company"
        private const val GAME_POINTS = "valoration"
        private const val GAME_STATUS = "status"
        private const val GAME_USERID = "userid"
        private const val GAME_IMAGE = "image"

        // Tabla seguidores
        private const val FOLLOWER_ID = "userid"
        private const val FOLLOWED_BY_ID = "followedbyid"

    }

    //Declaramos un singleton para no tener que hacer varias instancias
    fun getInstance(): MysqlManager {
        return instance
    }
    private var conn: Connection? = null // variable de conexion
    private var username = "android" // Usuario de la base de datos
    private var password = "Android1313!" // Contraseña de la base de datos

    //Metodo para conectar a la base de datos
    fun getConnection(): Boolean {
        var noError = true // variable para controlar si hay error
        val connectionProps = Properties() // variable para guardar las propiedades de la conexion
        connectionProps["user"] = username // asignamos el usuario de la base de datos
        connectionProps["password"] = password // asignamos la contraseña de la base de datos
        try {
            // intentamos conectar a la base de datos
            Class.forName("org.mariadb.jdbc.Driver").newInstance()
            conn = DriverManager.getConnection(
                "jdbc:" + "mariadb" + "://" +
                        "82.223.216.179" +
                        ":" + "3306" + "/" +
                        DATABASE_NAME,
                connectionProps)
        } catch (ex: SQLException) {
            // si hay error, lo guardamos en la variable noError
            noError = false
            ex.printStackTrace()
        } catch (ex: Exception) {
            // si hay error, lo guardamos en la variable noError
            noError = false
            ex.printStackTrace()
        }
        finally {
            // al finalizar sin error, y si no hay error, ejecutamos el metodo de generar tablas
            if (noError) {
                noError = executeMySQLQueryCreation()
            }
        }
        return noError
    }

    //Metodo para crear las tablas
    private fun executeMySQLQueryCreation(): Boolean {
        var stmt: Statement? = null // variable para ejecutar las consultas
        var noError = true // variable para controlar si hay error
        try {
            // intentamos ejecutar las consultas de las 3 tablas
            stmt = conn?.createStatement()
            var query = ("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                    + ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + NICK_USER + " varchar(64) NOT NULL, "
                    + PASSWORD_USER + " varchar(64) NOT NULL, "
                    + PRIVATE_USER + " INT NOT NULL DEFAULT 0"
                    + ")")
            stmt?.executeQuery(query)

            query = ("CREATE TABLE IF NOT EXISTS " + TABLE_USERGAMES + "("
                    + GAMEID + " INT NOT NULL, "
                    + GAME_STATUS + " varchar(64) NOT NULL,"
                    + GAME_POINTS + " INT NOT NULL,"
                    + GAME_PLATFORM + " varchar(64) NOT NULL,"
                    + GAME_USERID + " INT NOT NULL" + ")")
            stmt?.executeQuery(query)

            query = ("CREATE TABLE IF NOT EXISTS " + TABLE_GAMES + "("
                    + GAMEID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + GAME_NAME + " varchar(64) NOT NULL,"
                    + GAME_GENRE + " varchar(64) NOT NULL,"
                    + GAME_COMPANY + " varchar(64) NOT NULL,"
                    + GAMEVALIDATED + " INT NOT NULL DEFAULT 0,"
                    + GAME_IMAGE + " varchar(255)" + ")")
            stmt?.executeQuery(query)

            query = ("CREATE TABLE IF NOT EXISTS " + TABLE_FOLLOWERS + "("
                    + FOLLOWER_ID + " INT NOT NULL,"
                    + FOLLOWED_BY_ID + " INT NOT NULL" + ")")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            // si hay error, lo guardamos en la variable noError
            noError = false
            ex.printStackTrace()
        } finally {
            // al finalizar sin error, cerramos la conexion
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    //Metodo para comprobar si el usuario existe
    fun isValidUser(username: String, password: String): Boolean? {
        // comprobamos que username y password no esten vacios
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        var isValid: Boolean? // variable para controlar si hay error
        var stmt: Statement? = null // variable para ejecutar las consultas
        var queryResults: ResultSet? = null // variable para guardar los resultados de las consultas

        try {
            // intentamos ejecutar la consulta
            stmt = conn?.createStatement()
            val query = ("SELECT $NICK_USER FROM $TABLE_USER WHERE $NICK_USER = '$username' AND $PASSWORD_USER = '$password'")
            queryResults = stmt?.executeQuery(query)

            isValid = queryResults?.next() // si hay resultados, isValid es true

        } catch (ex: SQLException) {
            // si hay error, lo guardamos en la variable isValid
            isValid = null
            ex.printStackTrace()
        } finally {
            // al finalizar sin error, cerramos la conexion
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (queryResults != null) {
                try {
                    queryResults.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }

        return isValid
    }

    // Metodo para registrar un usuario
    fun addUser(user: User): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val query = ("INSERT INTO $TABLE_USER ($NICK_USER, $PASSWORD_USER) VALUES ('${user.name}', '${user.password}')")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    // Metodo para comprobar si el usuario ya existe
    fun checkUserExists(username: String): Boolean? {
        // comprobamos que username no este vacio
        if (username.isEmpty()) {
            return false
        }
        var isValid: Boolean?
        var stmt: Statement? = null
        var resultSet: ResultSet? = null

        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $NICK_USER FROM $TABLE_USER WHERE $NICK_USER = '$username'")
            resultSet = stmt?.executeQuery(query)
            
            isValid = resultSet?.next() == true

        } catch (ex: SQLException) {
            isValid = null
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }

        return isValid
    }

    // Metodo para obtener la lista de juegos de un usuario y ordenarlos
    fun getGamesPendingByUserid(userid: Int, order: String? = null, ascOrder: Boolean = true): ArrayList<Game>? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var games: ArrayList<Game>? = null
        try {
            stmt = conn?.createStatement()
            var query = ("SELECT T1.$GAME_NAME AS $GAME_NAME, T2.$GAME_STATUS AS $GAME_STATUS, T2.$GAME_PLATFORM AS $GAME_PLATFORM," +
                    "T1.$GAMEID AS $GAMEID, T1.$GAME_COMPANY AS $GAME_COMPANY," +
                    "T1.$GAME_GENRE AS $GAME_GENRE, T2.$GAME_POINTS AS $GAME_POINTS, T1.$GAME_IMAGE AS $GAME_IMAGE " +
                    "FROM $TABLE_GAMES T1 INNER JOIN $TABLE_USERGAMES T2 ON T1.$GAMEID = T2.$GAMEID WHERE T2.$GAME_USERID = $userid")
            if (order != null) {
                val orderBy = if (ascOrder) "ASC" else "DESC"
                query += " ORDER BY $order $orderBy"
            }
            resultSet = stmt?.executeQuery(query)

            games = ArrayList<Game>()
            while (resultSet?.next() == true) {
                games.add(
                    Game(
                        resultSet.getString(GAME_NAME),
                        resultSet.getString(GAME_STATUS),
                        resultSet.getString(GAME_PLATFORM),
                        resultSet.getInt(GAMEID),
                        resultSet.getString(GAME_COMPANY),
                        resultSet.getString(GAME_GENRE),
                        resultSet.getInt(GAME_POINTS),
                        resultSet.getString(GAME_IMAGE),
                    )
                )
            }

        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return games
    }

    // Metodo para obtener la id de un usuario
    fun getUserId(username: String): Int? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var userid: Int? = null
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $ID FROM $TABLE_USER WHERE $NICK_USER = '$username'")
            resultSet = stmt?.executeQuery(query)

            if (resultSet?.next() == true) {
                userid = resultSet.getInt(ID)
            }

        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return userid

    }

    // Metodo para añadir un juego a un usuario
    fun addGameByUserid(game: Game, userid: Int): Boolean {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        // comprobamos que la imagen no este vacia
        val image = if(game.image.isNullOrEmpty()) "" else game.image
        var noError = true
        try {
            stmt = conn?.createStatement()
            var query = "INSERT INTO $TABLE_GAMES ($GAME_NAME, $GAME_IMAGE, $GAME_COMPANY, $GAME_GENRE" +
                    ") VALUES ('${game.name}', '$image', '${game.company}', '${game.genre}')"
            stmt?.executeQuery(query)

            // obtenemos el id del juego insertado
            query = "SELECT LAST_INSERT_ID() AS $GAMEID"
            resultSet = stmt?.executeQuery(query)
            var gameid = 0
            if (resultSet?.next() == true) {
                gameid = resultSet.getInt(GAMEID)
            }
            // añadimos el juego al usuario en la tabla intermedia de relacion
            query = "INSERT INTO $TABLE_USERGAMES ($GAMEID, $GAME_STATUS, $GAME_POINTS, $GAME_USERID, $GAME_PLATFORM)" +
                    "VALUES ($gameid, '${game.status}', ${game.valoration}, $userid, '${game.platform}')"
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    // Metodo para actualizar los datos de un usuario
    fun updateUser(userid: Int, password: String): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val query = ("UPDATE $TABLE_USER SET $PASSWORD_USER = '$password' WHERE $ID = $userid")

            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    // Metodo para actualizar los datos de privacidad de un usuario
    fun updateUserStatus(userid: Int, private: Boolean): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val isPrivate = if(private) 1 else 0
            val query = ("UPDATE $TABLE_USER SET $PRIVATE_USER = $isPrivate WHERE $ID = $userid")

            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    // metodo para borrar un juego de un usuario
    fun deleteGame(gameid: Int): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            // borramos el juego de la tabla intermedia de relacion
            var query = ("DELETE FROM $TABLE_USERGAMES WHERE $GAMEID = $gameid")
            stmt?.executeQuery(query)

            // borramos el juego de la tabla de juegos
            query = ("DELETE FROM $TABLE_GAMES WHERE $GAMEID = $gameid")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    // Metodo para obtener el juego de un usuario
    fun getGameById(gameid: Int): Game? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var game: Game? = null
        try {
            stmt = conn?.createStatement()
            // Mediante innerjoins obtenemos el juego y su usuario
            val query = ("SELECT T1.$GAME_NAME AS $GAME_NAME, T2.$GAME_STATUS AS $GAME_STATUS, T2.$GAME_PLATFORM AS $GAME_PLATFORM," +
                    "T1.$GAMEID AS $GAMEID, T1.$GAME_COMPANY AS $GAME_COMPANY," +
                    "T1.$GAME_GENRE AS $GAME_GENRE, T2.$GAME_POINTS AS $GAME_POINTS, T1.$GAME_IMAGE AS $GAME_IMAGE " +
                    "FROM $TABLE_GAMES T1 INNER JOIN $TABLE_USERGAMES T2 ON T1.$GAMEID = T2.$GAMEID WHERE T1.$GAMEID = $gameid")
            resultSet = stmt?.executeQuery(query)

            if (resultSet?.next() == true) {
                game = Game(
                    resultSet.getString(GAME_NAME),
                    resultSet.getString(GAME_STATUS),
                    resultSet.getString(GAME_PLATFORM),
                    resultSet.getInt(GAMEID),
                    resultSet.getString(GAME_COMPANY),
                    resultSet.getString(GAME_GENRE),
                    resultSet.getInt(GAME_POINTS),
                    resultSet.getString(GAME_IMAGE),
                )
            }

        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return game
    }

    // Metodo para actualizar el juego de un usuario
    fun updateGame(game: Game): Boolean {
        var stmt: Statement? = null
        val image = if(game.image.isNullOrEmpty()) "" else game.image
        var noError = true
        try {
            stmt = conn?.createStatement()
            // actualizamos el juego en la tabla de juegos
            var query = ("UPDATE $TABLE_GAMES SET $GAME_NAME = '${game.name}', $GAME_IMAGE = '$image', $GAME_COMPANY = '${game.company}', $GAME_GENRE = '${game.genre}' WHERE $GAMEID = ${game.id}")
            stmt?.executeQuery(query)

            // actualizamos el juego en la tabla intermedia de relacion
            query = ("UPDATE $TABLE_USERGAMES SET $GAME_STATUS = '${game.status}', $GAME_POINTS = ${game.valoration}, $GAME_PLATFORM = '${game.platform}' WHERE $GAMEID = ${game.id}")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    // Metodo para obtener la lista de usuarios con perfil público
    fun getUserList(exception: Int): ArrayList<User>? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var users: ArrayList<User>? = null
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT * FROM $TABLE_USER" +
                    " WHERE $ID != $exception AND $PRIVATE_USER = 0")
            resultSet = stmt?.executeQuery(query)

            users = ArrayList<User>()
            while (resultSet?.next() == true) {
                users.add(User(
                    resultSet.getString(NICK_USER),
                    resultSet.getString(PASSWORD_USER),
                    resultSet.getInt(ID),
                    checkFollowed(exception, resultSet.getInt(ID)), // este user es seguidor tuyo
                    checkFollowed(resultSet.getInt(ID), exception) // sigues a este user
                ))
            }

        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return users
    }

    // Metodo para comprobar si un usuario sigue a otro
    private fun checkFollowed(userid: Int, followedbyid: Int): Boolean {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT * FROM $TABLE_FOLLOWERS WHERE $FOLLOWER_ID = $userid AND $FOLLOWED_BY_ID = $followedbyid")
            resultSet = stmt?.executeQuery(query)

            if (resultSet?.next() == true) {
                return true
            }

        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return false
    }

    fun unfollowBy(userId: Int, viewerId: Int): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val query = ("DELETE FROM $TABLE_FOLLOWERS WHERE $FOLLOWER_ID = $userId AND $FOLLOWED_BY_ID = $viewerId")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun followBy(userId: Int, viewerId: Int): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val query = ("INSERT INTO $TABLE_FOLLOWERS ($FOLLOWER_ID, $FOLLOWED_BY_ID) VALUES ($userId, $viewerId)")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun tryReconnect(): Boolean {
        if (conn == null || conn?.isClosed == true) {
            for (i in 0..5) {
                if (getConnection()) return true
            }
        }
        return false
    }

}