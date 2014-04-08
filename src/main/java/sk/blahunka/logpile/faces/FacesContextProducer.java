package sk.blahunka.logpile.faces;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

public class FacesContextProducer {

	@Produces
	@RequestScoped
	public FacesContext facesContext() {
		return FacesContext.getCurrentInstance();
	}

}
