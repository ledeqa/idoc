package com.idoc.code.def;

public class CommUtil
{

	public static String formatName(String name)
	{
		String[] split = name.split("_");
		if (split.length > 1)
		{
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < split.length; i++)
			{
				String tempName = "";
				if (i == 0)
				{
					tempName = split[0].substring(0, 1).toLowerCase() + split[0].substring(1, split[0].length());
					sb.append(tempName);
					continue;
				}
				tempName = split[i].substring(0, 1).toUpperCase() + split[i].substring(1, split[i].length());
				sb.append(tempName);
			}

			return sb.toString().toLowerCase();
		}
		String tempName = split[0].toLowerCase();
		return tempName;
	}
}
