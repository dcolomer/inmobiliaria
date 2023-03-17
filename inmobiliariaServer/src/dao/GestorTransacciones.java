package dao;
/*
 * Clase encargada de encapsular el manejo de transacciones.
 * Es responsable de la transaccionabilidad de las operaciones.
 * 
 * Ejemplo de uso:
 * 
 * GestorTransacciones gestorTran = .. // codigo de inicializacion
 *
 * try {
 * 		gestorTran.start();
 *		dao1.insert(...);
 *		dao2.update(....);
 *		dao2.delete(...)
 *		dao3.update(..);
 *		dao1.insert(...);
 *		gestorTran.commit();
 * } catch (Exception e) {
 * 		gestorTran.rollback();
 * } finally {
 * 		gestorTran.end();
 * }
 * 
 */

public interface GestorTransacciones {

	/*
	 * Invocar a este metodo cuando se inicia la transaccion. 
	 * Deberia contener todas las precondiciones necesarias 
	 * para ejecutar la transaccion.
	 */
	public void start();

	/*
	 * Realiza la confirmacion de la transaccion
	 */
	public void commit();

	/*
	 * Deshace la transaccion
	 */
	public void rollback();

	/*
	 * Invocar a este metodo para finalizar la transaccion, 
	 * suceda o no un error.
	 * Hay que volver al estado anterior a la transaccion y 
	 * cerrar los recursos solicitados al inicio de la misma
	 */
	public void end();
}
