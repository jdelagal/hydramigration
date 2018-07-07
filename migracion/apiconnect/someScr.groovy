def command = """
	redis-cli \
	hmset 111 secreto 111 entorno PRE
			  """
println command.execute().text