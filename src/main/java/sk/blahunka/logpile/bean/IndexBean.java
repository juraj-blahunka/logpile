package sk.blahunka.logpile.bean;

import com.google.common.collect.Ordering;
import sk.blahunka.logpile.LogPile;
import sk.blahunka.logpile.dto.LogErrorSummary;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Named
@RequestScoped
public class IndexBean {

	@Inject
	LogPile logPile;

	@Inject
	FacesContext facesContext;

	private Part file;
	private List<LogErrorSummary> errors;

	public void upload() {
		if (file == null) {
			facesContext.addMessage(null, new FacesMessage("Must select a file"));
			return;
		}

		try (InputStream inputStream = file.getInputStream()) {
			List<LogErrorSummary> listOfErrors = logPile.errors(inputStream);

			errors = Ordering.from(LogErrorSummary.BY_NUMBER_OF_TOTAL_LOG_MESSAGES)
					.reverse()
					.sortedCopy(listOfErrors);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public List<LogErrorSummary> getErrors() {
		return errors;
	}

	public boolean isDataPresent() {
		return errors != null;
	}

}
