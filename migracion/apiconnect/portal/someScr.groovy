def command = """
	redis-cli \
	hmset 111 secreto 111 entorno dev
			  """
println command.execute().text