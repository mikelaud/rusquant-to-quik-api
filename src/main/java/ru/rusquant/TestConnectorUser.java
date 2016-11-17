package ru.rusquant;


import ru.rusquant.connector.JavaToQuikConnector;
import ru.rusquant.connector.JavaToQuikPipeConnector;
import ru.rusquant.data.quik.ConnectionState;
import ru.rusquant.data.quik.Echo;
import ru.rusquant.data.quik.ErrorObject;
import ru.rusquant.data.quik.QuikDataObject;
import ru.rusquant.messages.request.RequestSubject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Just for testing
 * Created by kutergin on 30.09.2016.
 */
public class TestConnectorUser
{
	private static SecureRandom random = new SecureRandom();

	public static void main(String[] args) throws InterruptedException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		JavaToQuikConnector connector = new JavaToQuikPipeConnector();


		boolean isExit = false;
		while(!isExit)
		{
			System.out.println();
			System.out.println("Enter any key to try to connect or type exit");

			String command = reader.readLine();
			if("exit".equals(command))
			{
				isExit = true;
			}
			else
			{
				connector.connect();
				if(connector.isConnectedToServer())
				{
					showTestsMenu(connector, reader);
				}
				else
				{
					System.out.println(connector.getConnectorError().getErrorMessage());
				}
			}
		}

		reader.close();
	}


	private static void showTestsMenu(JavaToQuikConnector connector, BufferedReader reader) throws IOException
	{
		boolean isExit = false;
		while(!isExit)
		{
			System.out.println();
			System.out.println("Select the test type: manual or auto or exit");
			String test = reader.readLine();
			if("manual".equals(test))
			{
				runManualTest(connector, reader);
			}
			else if("auto".equals(test))
			{
				runAutoTest(connector);
			}
			else if("exit".equals(test))
			{
				isExit = true;
			}
			else
			{
				System.out.println("Invalid test type!");
			}
		}
		connector.disconnect();
	}


	private static void runManualTest(JavaToQuikConnector connector, BufferedReader reader) throws IOException
	{
		System.out.println("Running manual test...");
		System.out.println();

		boolean isExit = false;
		while(!isExit)
		{
			System.out.println();
			System.out.println("Select the type of information:");
			System.out.println("\techo (get echo from server)");
			System.out.println("\tinfo (get info about terminal)");
			System.out.println("\tisconnected (status of connection between terminal and QUIK-server)");
			System.out.println("\texit");
			System.out.println();

			String message = reader.readLine();
			if(message != null && !message.isEmpty())
			{
				if("exit".equals(message))
				{
					isExit = true;
				}
				else if("echo".equals(message))
				{
					runEchoTest(connector, reader);
				}
				else if("info".equals(message))
				{

				}
				else if("isconnected".equals(message))
				{
					QuikDataObject result = connector.isConnected();
					if(result instanceof ErrorObject)
					{
						System.out.println( ((ErrorObject) result).getErrorMessage() );
						isExit = true;
					}
					else
					{
						if(((ConnectionState) result).isConnected().equals(1))
						{
							System.out.println();
							System.out.println("Terminal is connected to QUIK-server");
							System.out.println();
						}
						else
						{
							System.out.println();
							System.out.println("Terminal is not connected to QUIK-server");
							System.out.println();
						}
					}
				}
				else
				{
					System.out.println("Invalid type of request!");
				}
			}
			else
			{
				System.out.println("Invalid test type!");
			}
		}

	}


	private static void runEchoTest(JavaToQuikConnector connector, BufferedReader reader) throws IOException
	{
		System.out.println("Running echo test...");
		System.out.println();

		boolean isExit = false;
		while(!isExit)
		{
			System.out.println();
			System.out.println("Enter not empty message message or type exit:");
			String message = reader.readLine();
			if(message != null && !message.isEmpty())
			{
				if("exit".equals(message))
				{
					isExit = true;
				}
				else
				{
					QuikDataObject result = connector.getEcho(message);
					if(result instanceof ErrorObject)
					{
						System.out.println( ((ErrorObject) result).getErrorMessage() );
						isExit = true;
					}
					else
					{
						System.out.println( ((Echo) result).getEchoAnswer() );
					}
				}
			}
			else
			{
				System.out.println("Invalid test type!");
			}
		}
	}


	private static void runAutoTest(JavaToQuikConnector connector)
	{
		System.out.println("Running auto echo test...");
		System.out.println();

		long startTime = System.currentTimeMillis();
		int count = 100000;
		for(int i = 0; i < count; i++)
		{
			String message = getRandomString();
			if(message != null && !message.isEmpty())
			{
				QuikDataObject result = connector.getEcho(message);
				if(result instanceof ErrorObject)
				{
					System.out.println( ((ErrorObject) result).getErrorMessage() );
					break;
				}
				else
				{
					String answer = ((Echo) result).getEchoAnswer();
					if(!answer.contains(message))
					{
						System.out.println("Wrong answer received!");
						break;
					}
					else
					{
						if( i % (0.10 * count) == 0) { System.out.print("*"); }
					}
				}
			}
			else
			{
				System.out.println("Invalid message!");
				break;
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("\nConnector process " + count + " of messages in " + (endTime - startTime) + " milliseconds");
		System.out.println("With average shipping duration of request: " 	+ connector.getAvgShippingDurationOfRequest(RequestSubject.ECHO));
		System.out.println("With average duration of request processing: " 	+ connector.getAvgDurationOfRequestProcessing(RequestSubject.ECHO));
		System.out.println("With average shipping duration of response: " 	+ connector.getAvgShippingDurationOfResponse(RequestSubject.ECHO));
		System.out.println("With average request response latency: " 		+ connector.getAvgRequestResponseLatency(RequestSubject.ECHO));
		System.out.println();
	}


	private static String getRandomString()
	{
		return new BigInteger(130, random).toString();
	}

}