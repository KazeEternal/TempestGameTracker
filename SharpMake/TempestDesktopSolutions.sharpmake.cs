using Sharpmake;

[module: Sharpmake.Include("TempestDesktopProject.sharpmake.cs")]
[module: Sharpmake.Include("TempestServiceProject.sharpmake.cs")]
[module: Sharpmake.Include("Tempest.Common.sharpmake.cs")]

namespace WindowsCSharp
{
	[Generate]
    public class TempestGenerator
    {
        [Main]
		public static void SharpmakeMain(Arguments sharpmakeArgs)
		{
            // Tells Sharpmake to generate the solution described by
            // BasicsSolution.
            sharpmakeArgs.Generate<TempestSolution>();
		}
    }

	[Generate]
	public class TempestSolution : Solution
	{
		public TempestSolution()//: base(Target.
		{
			Name = "Tempest";
            AddTargets(Tempest.Common.Targets);
        }
		
		[Configure]
        public void ConfigureAll(Configuration conf, Sharpmake.Target target)
		{
            
			conf.SolutionFileName = "[solution.Name]_[target.DevEnv]";
			conf.SolutionPath = @"[solution.SharpmakeCsPath]\..\Windows\";
			conf.AddProject<TempestDesktopProject>(target);
			conf.AddProject<TempestServiceProject>(target);
			conf.Options.Add(Options.Vc.Compiler.Exceptions.Enable);
		}
	}
}