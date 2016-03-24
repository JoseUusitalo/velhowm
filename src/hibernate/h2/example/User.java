package hibernate.h2.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User
{

	private int id;
	private String name;

	public User()
	{
		// this form used by Hibernate
	}

	public User(final int id, final String name)
	{
		this.id = id;
		this.name = name;
	}

	@Id
	public int getId()
	{
		return id;
	}

	public void setId(final int id)
	{
		this.id = id;
	}

	@Column(name = "name")
	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}
}
