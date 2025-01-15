package fr.isep.ye.projet_algo_jjx;

import java.sql.Connection;

public interface connection {
    Connection connect();
    void close(Connection connection);
}
