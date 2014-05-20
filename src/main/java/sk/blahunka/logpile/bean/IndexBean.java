package sk.blahunka.logpile.bean;

import com.google.common.collect.Ordering;
import sk.blahunka.logpile.dto.LogErrorSummary;
import sk.blahunka.logpile.dto.LogMessages;
import sk.blahunka.logpile.LogPile;
import sk.blahunka.logpile.ast.LogStatement;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
			List<LogErrorSummary> listOfErrors = logPile(inputStream);

			errors = Ordering.from(LogErrorSummary.BY_NUMBER_OF_TOTAL_LOG_MESSAGES)
					.reverse()
					.sortedCopy(listOfErrors);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<LogErrorSummary> logPile(InputStream inputStream) {
		List<LogStatement> statements = logPile.parseLogStatemens(inputStream);
		List<LogStatement> errors = logPile.filterErrors(statements);
		Map<LogPile.StackTraceKey, LogMessages> uniqueErrors = logPile.errorsWithSameOrigin(errors);
		return logPile.categorizeErrors(uniqueErrors);
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
