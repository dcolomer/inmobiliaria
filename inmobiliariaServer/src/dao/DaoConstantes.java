package dao;

/**
 * Enumeracion con dos constantes:
 * TransaccionesOFF: no queremos comportamiento transaccional para un Dao
 * TransaccionesON: Queremos comportamiento transaccional para un Dao
 */
public enum DaoConstantes {
	TransaccionesOFF, TransaccionesON;
}
