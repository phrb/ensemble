package mms;

import java.io.Serializable;

import mms.clock.TimeUnit;

public class Event implements Serializable {

	// Identificação do Evento
	// qual agente gerou, qual o componente e qual o tipo de evento
	public String	destAgentName;
	public String	destAgentCompName;
	public String	oriAgentName;
	public String	oriAgentCompName;
	public String	eventType;

	// Timestamp do evento. No caso de um evento periódico, corresponde ao instante inicial do frame, caso contrário, é o instante em que foi enviado
	// TODO não necessariamente precisa ser o tempo em ms, pode ser o turno
	public long 	timestamp;
	
	// No caso de eventos periódicos, indica a qual frame esse evento pertence
	public long 	frame;
	
	// Instante e duração no tempo (em s) em que o evento acontece
	// No caso de eventos periódicos, corresponde ao ínico do frame, caso contrário, ao momento em que foi enviado
	public double 	instant;
	public double 	duration;
	public TimeUnit unit;
	
	// Conteúdo do evento (caso necessite de algo mais complexo, estender a classe)
	public Object 	objContent;
	
	public Event() {
	}
	
	public String toString() {
		String str;
		str = oriAgentName + ":" + oriAgentCompName + " -> " + destAgentName + ":" + destAgentCompName + " / frame = " + frame + " instant = " + instant + " duration = " + duration;
		return str;
	}
	
	// TODO Transforma um evento em uma mensagem ACL
	public String toACLMessage() {
		return null;
	}
	
}
