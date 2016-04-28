package velhotest.view;

interface UserInput {
	public String userFnameField.getText();
}

class TestUserInput implements UserInput {

	private String createUser;
	createUser = createUser_;
	}

	public String getUserName() {
		return createUser;
	}
}

	@Test
	public void should_initialize_countValue() {
		verifyThat("#countValue", hasText("0"));
	}

	@Test
	public void should_increment_countValue() {
		click("Create user");
		verifyThat("#countValue", hasText("1"));
	}