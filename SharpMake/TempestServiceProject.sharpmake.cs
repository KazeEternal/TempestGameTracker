using Sharpmake;

[module: Sharpmake.Include("Tempest.Common.sharpmake.cs")]

namespace WindowsCSharp
{	
	[Generate]
	public class TempestServiceProject : CSharpProject
	{
		public TempestServiceProject()
		{
			Name = "Service";
			
			AddTargets(
				Tempest.Common.Targets
			);
			
			SourceRootPath = @"[project.SharpmakeCsPath]\..\Windows\[project.Name]";
		}
		
		[Configure()]
		public void ConfigureAll(Configuration conf, Target target)
		{
			conf.SolutionFolder = "Windows";
			
			conf.Output = Configuration.OutputType.DotNetWindowsApp;

            conf.ReferencesByName.AddRange(
                new Strings(
                    "System",
                    "System.Core",
                    "System.Xml.Linq",
                    "System.Data.DataSetExtensions",
                    "System.Data",
                    "System.Xaml",
                    "System.Xml",
                    "PresentationCore",
                    "PresentationFramework",
                    "WindowsBase"
                )
            );

            conf.ProjectFileName = "[project.Name]_[target.DevEnv]";
			conf.ProjectPath = @"[project.SharpmakeCsPath]\..\Windows\[project.Name]";
			
			conf.TargetPath = @"[project.SourceRootPath]\..\..\[project.Name]";


            if (target.Optimization == Optimization.Debug)
                conf.Options.Add(Options.Vc.Compiler.RuntimeLibrary.MultiThreadedDebugDLL);
            else
                conf.Options.Add(Options.Vc.Compiler.RuntimeLibrary.MultiThreadedDLL);

            conf.Options.Add(Sharpmake.Options.CSharp.TreatWarningsAsErrors.Enabled);

        }
	}
}